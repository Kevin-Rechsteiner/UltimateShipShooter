package dev.zwazel.game.game.websocket;

import dev.zwazel.game.game.util.GameConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketEchoHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int GAME_WIDTH = 720;
    private static final int GAME_HEIGHT = 520;
    private static final int PLAYER_WIDTH = 52;
    private static final int PLAYER_HEIGHT = GameConstants.shipHeight;
    private static final int INITIAL_LIVES = 3;

    private final Map<String, GameSessionState> sessionStates = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();

    private final BulletManager bulletManager = new BulletManager();
    private final AsteroidManager asteroidManager = new AsteroidManager();
    private final CollisionDetector collisionDetector = new CollisionDetector();
    private final GameStateUpdater stateUpdater = new GameStateUpdater();

    private final ScheduledExecutorService gameTick = Executors.newSingleThreadScheduledExecutor();
    private volatile long lastTickNanos = System.nanoTime();

    public WebSocketEchoHandler() {
        gameTick.scheduleAtFixedRate(this::tick, 0, 16, TimeUnit.MILLISECONDS);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        double initialX = (GAME_WIDTH - PLAYER_WIDTH) / 2.0;
        sessionsById.put(session.getId(), session);
        sessionStates.put(session.getId(), new GameSessionState(initialX, INITIAL_LIVES, 0));

        Map<String, Object> statePayload = stateUpdater.buildInitialStatePayload(initialX);
        Map<String, Object> stateEvent = new HashMap<>();
        stateEvent.put("type", "state");
        stateEvent.put("payload", statePayload);

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(stateEvent)));
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws IOException {
        Map<String, Object> incoming = objectMapper.readValue(
                message.getPayload(),
                new TypeReference<Map<String, Object>>() {}
        );

        String type = String.valueOf(incoming.get("type"));
        Object payloadObj = incoming.get("payload");

        if ("ping".equals(type) && payloadObj instanceof Map<?, ?> payload) {
            Map<String, Object> pong = new HashMap<>();
            pong.put("type", "pong");
            pong.put("payload", payload);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pong)));
            return;
        }

        GameSessionState state = sessionStates.get(session.getId());
        if (state == null) return;

        if ("player_input".equals(type) && payloadObj instanceof Map<?, ?> payload) {
            boolean left = Boolean.TRUE.equals(payload.get("left")) || Boolean.TRUE.equals(payload.get("a"));
            boolean right = Boolean.TRUE.equals(payload.get("right")) || Boolean.TRUE.equals(payload.get("d"));
            boolean shoot = Boolean.TRUE.equals(payload.get("shoot"))
                    || Boolean.TRUE.equals(payload.get("space"))
                    || Boolean.TRUE.equals(payload.get("fire"));

            state.setLeftPressed(left);
            state.setRightPressed(right);
            state.setShootPressed(shoot);
            return;
        }

        if ("game_control".equals(type) && payloadObj instanceof Map<?, ?> payload) {
            if (Boolean.TRUE.equals(payload.get("restart")) || Boolean.TRUE.equals(payload.get("reset"))) {
                resetGameState(state);
                sendStateUpdate(session, state);
                return;
            }

            if (payload.containsKey("paused")) {
                state.setPaused(parseBoolean(payload.get("paused")));
            } else if (Boolean.TRUE.equals(payload.get("toggle_pause"))) {
                state.setPaused(!state.isPaused());
            }
            sendStateUpdate(session, state);
            return;
        }

        Map<String, Object> fallback = new HashMap<>();
        fallback.put("type", "pong");
        fallback.put("payload", Map.of("at", System.currentTimeMillis()));
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(fallback)));
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessionsById.remove(session.getId());
        sessionStates.remove(session.getId());
    }

    private void tick() {
        long now = System.nanoTime();
        double deltaSeconds = (now - lastTickNanos) / 1_000_000_000.0;
        lastTickNanos = now;
        deltaSeconds = Math.min(deltaSeconds, 0.05);

        for (Map.Entry<String, GameSessionState> entry : sessionStates.entrySet()) {
            String sessionId = entry.getKey();
            GameSessionState state = entry.getValue();
            WebSocketSession session = sessionsById.get(sessionId);

            if (session == null || !session.isOpen()) continue;

            // Game Over → Pause
            if (state.isGameOver()) {
                state.setPaused(true);
            }

            // Wenn pausiert → nur State senden, keine Logik
            if (state.isPaused()) {
                sendStateUpdate(session, state);
                continue;
            }

            // Bewegung
            int direction = (state.isLeftPressed() ? -1 : 0) + (state.isRightPressed() ? 1 : 0);
            double nextX = state.getPlayerX();
            if (direction != 0) {
                nextX = clamp(
                        state.getPlayerX() + direction * 320.0 * deltaSeconds,
                        0,
                        GAME_WIDTH - PLAYER_WIDTH
                );
                state.setPlayerX(nextX);
            }

            // Bullets
            long nowMillis = System.currentTimeMillis();
            bulletManager.updateBullets(
                    state.getBullets(),
                    state.getPlayerX(),
                    state.isShootPressed(),
                    state.getLastShotMillis(),
                    nowMillis,
                    deltaSeconds
            );
            state.setLastShotMillis(bulletManager.getLastShotMillis(
                    state.isShootPressed(),
                    state.getLastShotMillis(),
                    nowMillis
            ));

            // Asteroids
            asteroidManager.updateAsteroids(
                    state.getAsteroids(),
                    state.getLastAsteroidSpawnMillis(),
                    nowMillis,
                    deltaSeconds
            );
            state.setLastAsteroidSpawnMillis(asteroidManager.getLastSpawnMillis(
                    state.getLastAsteroidSpawnMillis(),
                    nowMillis
            ));

            // Kollisionen
            CollisionDetector.CollisionResult collisions = collisionDetector.detectCollisions(
                    state.getBullets(),
                    state.getAsteroids(),
                    state.getPlayerX()
            );

            // Damage entfernen
            state.getBullets().removeIf(b -> collisions.hitBulletIds.contains(b.id()));

            int scoreGain = 0;
            if (!collisions.bulletHitsPerAsteroidId.isEmpty()) {
                List<AsteroidState> updatedAsteroids = new ArrayList<>(state.getAsteroids().size());
                for (AsteroidState asteroid : state.getAsteroids()) {
                    int hits = collisions.bulletHitsPerAsteroidId.getOrDefault(asteroid.id(), 0);
                    int nextHp = asteroid.hp() - hits;

                    if (nextHp <= 0) {
                        scoreGain += scoreForAsteroidSize(asteroid.size());
                        continue;
                    }

                    if (hits > 0) {
                        updatedAsteroids.add(new AsteroidState(
                                asteroid.id(),
                                asteroid.x(),
                                asteroid.y(),
                                asteroid.size(),
                                nextHp
                        ));
                    } else {
                        updatedAsteroids.add(asteroid);
                    }
                }
                state.setAsteroids(updatedAsteroids);
            }

            state.getAsteroids().removeIf(a -> collisions.shipHitAsteroidIds.contains(a.id()));

            // Score + Leben
            state.setScore(state.getScore() + scoreGain);
            if (state.getScore() > state.getHighScore()) {
                state.setHighScore(state.getScore());
            }
            if (collisions.shipHitCount > 0) {
                state.setLives(Math.max(0, state.getLives() - collisions.shipHitCount));
            }

            sendStateUpdate(session, state);
        }
    }


    private void sendStateUpdate(WebSocketSession session, GameSessionState state) {
        if (!session.isOpen()) return;

        Map<String, Object> payload = stateUpdater.buildStatePayload(
                state,
                state.getBullets(),
                state.getAsteroids()
        );

        Map<String, Object> event = new HashMap<>();
        event.put("type", "state");
        event.put("payload", payload);

        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
        } catch (IOException ignored) {
            // Connection cleanup happens in afterConnectionClosed
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private boolean parseBoolean(Object value) {
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return Boolean.parseBoolean(s);
        return false;
    }

    private void resetGameState(GameSessionState state) {
        state.setPlayerX((GAME_WIDTH - PLAYER_WIDTH) / 2.0);
        state.setLeftPressed(false);
        state.setRightPressed(false);
        state.setShootPressed(false);
        state.getBullets().clear();
        state.getAsteroids().clear();
        state.setLastShotMillis(0L);
        state.setLastAsteroidSpawnMillis(0L);
        state.setLives(INITIAL_LIVES);
        state.setScore(0);
        state.setPaused(false);
    }

    private int scoreForAsteroidSize(int size) {
        if (size <= 24) return 30;
        if (size <= 32) return 20;
        return 10;
    }
}
