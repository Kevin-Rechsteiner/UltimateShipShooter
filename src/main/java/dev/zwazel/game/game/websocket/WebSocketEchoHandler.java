package dev.zwazel.game.game.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketEchoHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int GAME_WIDTH = 720;
    private static final int GAME_HEIGHT = 520;
    private static final int PLAYER_WIDTH = 52;
    private static final int PLAYER_HEIGHT = 90;
    private static final int PLAYER_Y = GAME_HEIGHT - PLAYER_HEIGHT - 20;
    private static final double PLAYER_SPEED_PX_PER_SECOND = 320.0;

    private final Map<String, PlayerInputState> stateBySession = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();
    private final ScheduledExecutorService gameTick = Executors.newSingleThreadScheduledExecutor();
    private volatile long lastTickNanos = System.nanoTime();

    public WebSocketEchoHandler() {
        // Server-authoritative movement tick: keeps moving while key is held.
        gameTick.scheduleAtFixedRate(this::tick, 0, 16, TimeUnit.MILLISECONDS);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        double initialX = (GAME_WIDTH - PLAYER_WIDTH) / 2.0;
        sessionsById.put(session.getId(), session);
        stateBySession.put(session.getId(), new PlayerInputState(initialX, false, false));

        Map<String, Object> statePayload = new HashMap<>();
        statePayload.put("width", GAME_WIDTH);
        statePayload.put("height", GAME_HEIGHT);

        Map<String, Object> player = new HashMap<>();
        player.put("x", (int) Math.round(initialX));
        player.put("y", PLAYER_Y);
        player.put("width", PLAYER_WIDTH);
        player.put("height", PLAYER_HEIGHT);
        player.put("speed", 320);

        statePayload.put("player", player);
        statePayload.put("asteroids", new Object[]{});

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
            pong.put("payload", payload); // echo { at }

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pong)));
            return;
        }

        if ("player_input".equals(type) && payloadObj instanceof Map<?, ?> payload) {
            // Accept both {left,right} and {s,d} naming from frontend.
            boolean left = Boolean.TRUE.equals(payload.get("left")) || Boolean.TRUE.equals(payload.get("s"));
            boolean right = Boolean.TRUE.equals(payload.get("right")) || Boolean.TRUE.equals(payload.get("d"));

            stateBySession.computeIfPresent(
                    session.getId(),
                    (id, current) -> new PlayerInputState(current.x(), left, right)
            );
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
        stateBySession.remove(session.getId());
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void tick() {
        long now = System.nanoTime();
        double deltaSeconds = (now - lastTickNanos) / 1_000_000_000.0;
        lastTickNanos = now;
        // Prevent huge jumps after debugger pauses or temporary stalls.
        deltaSeconds = Math.min(deltaSeconds, 0.05);

        for (Map.Entry<String, PlayerInputState> entry : stateBySession.entrySet()) {
            String sessionId = entry.getKey();
            PlayerInputState current = entry.getValue();
            WebSocketSession session = sessionsById.get(sessionId);

            if (session == null || !session.isOpen()) {
                continue;
            }

            int direction = (current.left() ? -1 : 0) + (current.right() ? 1 : 0);
            if (direction == 0) {
                continue;
            }

            double nextX = clamp(
                    current.x() + direction * PLAYER_SPEED_PX_PER_SECOND * deltaSeconds,
                    0,
                    GAME_WIDTH - PLAYER_WIDTH
            );
            stateBySession.put(sessionId, new PlayerInputState(nextX, current.left(), current.right()));

            Map<String, Object> payload = new HashMap<>();
            payload.put("player", Map.of("x", (int) Math.round(nextX)));

            Map<String, Object> event = new HashMap<>();
            event.put("type", "state");
            event.put("payload", payload);

            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
            } catch (IOException ignored) {
                // Connection will be cleaned up by close handling.
            }
        }
    }

    private record PlayerInputState(double x, boolean left, boolean right) {
    }
}
