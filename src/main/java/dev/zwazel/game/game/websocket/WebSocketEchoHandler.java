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
    private static final long ACTIVE_STATE_PUSH_INTERVAL_NANOS = 16_000_000L;
    private static final long PAUSED_STATE_PUSH_INTERVAL_NANOS = 120_000_000L;

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
        GameSessionState state = new GameSessionState(initialX, INITIAL_LIVES, 0);
        sessionsById.put(session.getId(), session);
        sessionStates.put(session.getId(), state);

        Map<String, Object> statePayload = stateUpdater.buildInitialStatePayload(initialX);
        Map<String, Object> stateEvent = new HashMap<>();
        stateEvent.put("type", "state");
        stateEvent.put("payload", statePayload);

        state.setLastStatePushNanos(System.nanoTime());
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
        Map<?, ?> resolvedPayload = payloadObj instanceof Map<?, ?> payloadMap ? payloadMap : incoming;

        if ("ping".equals(type) && payloadObj instanceof Map<?, ?> payload) {
            Map<String, Object> pong = new HashMap<>();
            pong.put("type", "pong");
            pong.put("payload", payload);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pong)));
            return;
        }

        GameSessionState state = sessionStates.get(session.getId());
        if (state == null) return;

        if (isPlayerInputType(type)) {
            boolean left = readInputFlag(resolvedPayload, "left", "a", "s", "ArrowLeft", "keyA", "KeyA", "keyS", "KeyS", "moveLeft");
            boolean right = readInputFlag(resolvedPayload, "right", "d", "ArrowRight", "keyD", "KeyD", "moveRight");
            boolean shoot = readInputFlag(resolvedPayload, "shoot", "space", "fire", "Space", " ", "shootPressed", "isShooting");

            double horizontal = readInputAxis(resolvedPayload, "horizontal", "x", "moveX", "axisX");
            if (horizontal < -0.001) left = true;
            if (horizontal > 0.001) right = true;

            synchronized (state) {
                state.setLeftPressed(left);
                state.setRightPressed(right);
                state.setShootPressed(shoot);
            }
            return;
        }

        if ("game_control".equals(type) && payloadObj instanceof Map<?, ?> payload) {
            if (Boolean.TRUE.equals(payload.get("restart")) || Boolean.TRUE.equals(payload.get("reset"))) {
                synchronized (state) {
                    resetGameState(state);
                }
                sendStateUpdate(session, state, true);
                return;
            }

            if (payload.containsKey("paused")) {
                synchronized (state) {
                    state.setPaused(parseBoolean(payload.get("paused")));
                }
            } else if (Boolean.TRUE.equals(payload.get("toggle_pause"))) {
                synchronized (state) {
                    state.setPaused(!state.isPaused());
                }
            }
            sendStateUpdate(session, state, true);
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

        try {
            for (Map.Entry<String, GameSessionState> entry : sessionStates.entrySet()) {
                String sessionId = entry.getKey();
                GameSessionState state = entry.getValue();
                WebSocketSession session = sessionsById.get(sessionId);

                if (session == null || !session.isOpen()) continue;

                synchronized (state) {
                    if (state.isGameOver()) {
                        state.setPaused(true);
                    }

                    if (state.isPaused()) {
                        sendStateUpdate(session, state, false);
                        continue;
                    }

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

                    CollisionDetector.CollisionResult collisions = collisionDetector.detectCollisions(
                            state.getBullets(),
                            state.getAsteroids(),
                            state.getPlayerX()
                    );

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

                    state.setScore(state.getScore() + scoreGain);
                    if (state.getScore() > state.getHighScore()) {
                        state.setHighScore(state.getScore());
                    }
                    if (collisions.shipHitCount > 0) {
                        state.setLives(Math.max(0, state.getLives() - collisions.shipHitCount));
                    }

                    sendStateUpdate(session, state, false);
                }
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    private void sendStateUpdate(WebSocketSession session, GameSessionState state, boolean force) {
        if (!session.isOpen()) return;

        if (!force && !shouldBroadcastNow(state)) {
            return;
        }

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
            state.setLastStatePushNanos(System.nanoTime());
        } catch (IOException ignored) { }
    }

    private boolean shouldBroadcastNow(GameSessionState state) {
        long now = System.nanoTime();
        long interval = state.isPaused() ? PAUSED_STATE_PUSH_INTERVAL_NANOS : ACTIVE_STATE_PUSH_INTERVAL_NANOS;
        return (now - state.getLastStatePushNanos()) >= interval;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private boolean parseBoolean(Object value) {
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return Boolean.parseBoolean(s);
        return false;
    }

    private boolean isPlayerInputType(String type) {
        return "player_input".equals(type)
                || "playerInput".equals(type)
                || "input".equals(type)
                || "key_input".equals(type)
                || "controls".equals(type);
    }

    private boolean readInputFlag(Map<?, ?> payload, String... keys) {
        if (hasTruthyValue(payload, keys)) {
            return true;
        }

        Object nestedInput = payload.get("input");
        if (nestedInput instanceof Map<?, ?> nestedMap && hasTruthyValue(nestedMap, keys)) {
            return true;
        }

        Object nestedKeys = payload.get("keys");
        if (nestedKeys instanceof Map<?, ?> nestedMap && hasTruthyValue(nestedMap, keys)) {
            return true;
        }

        Object nestedMovement = payload.get("movement");
        return nestedMovement instanceof Map<?, ?> nestedMap && hasTruthyValue(nestedMap, keys);
    }

    private boolean hasTruthyValue(Map<?, ?> payload, String... keys) {
        for (String key : keys) {
            if (toBoolean(payload.get(key))) {
                return true;
            }
        }
        return false;
    }

    private double readInputAxis(Map<?, ?> payload, String... keys) {
        Double direct = findNumericValue(payload, keys);
        if (direct != null) return direct;

        Object nestedInput = payload.get("input");
        if (nestedInput instanceof Map<?, ?> nestedMap) {
            Double nested = findNumericValue(nestedMap, keys);
            if (nested != null) return nested;
        }

        Object nestedMovement = payload.get("movement");
        if (nestedMovement instanceof Map<?, ?> nestedMap) {
            Double nested = findNumericValue(nestedMap, keys);
            if (nested != null) return nested;
        }

        return 0.0;
    }

    private Double findNumericValue(Map<?, ?> payload, String... keys) {
        for (String key : keys) {
            Object value = payload.get(key);
            if (value instanceof Number number) {
                return number.doubleValue();
            }
            if (value instanceof String stringValue) {
                try {
                    return Double.parseDouble(stringValue);
                } catch (NumberFormatException ignored) { }
            }
        }
        return null;
    }

    private boolean toBoolean(Object value) {
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        if (value instanceof Number number) {
            return number.doubleValue() != 0.0;
        }
        if (value instanceof String stringValue) {
            String normalized = stringValue.trim().toLowerCase();
            return "true".equals(normalized) || "1".equals(normalized) || "yes".equals(normalized) || "on".equals(normalized);
        }
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
