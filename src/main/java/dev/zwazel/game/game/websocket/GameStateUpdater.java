package dev.zwazel.game.game.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Erstellt das State-Update für WebSocket-Clients.
 * Konvertiert interne States zu JSON-Payload.
 */
public class GameStateUpdater {
    private static final int BULLET_SIZE = 9;
    private static final int GAME_WIDTH = 720;
    private static final int GAME_HEIGHT = 520;
    private static final int PLAYER_WIDTH = 52;
    private static final int PLAYER_HEIGHT = 30;
    private static final int PLAYER_Y = GAME_HEIGHT - PLAYER_HEIGHT - 20;

    public Map<String, Object> buildStatePayload(
            GameSessionState sessionState,
            List<BulletState> bullets,
            List<AsteroidState> asteroids
    ) {
        Map<String, Object> payload = new HashMap<>();

        // Player
        payload.put("player", Map.of(
                "x", (int) Math.round(sessionState.getPlayerX())
        ));

        // Bullets
        List<Map<String, Object>> bulletsPayload = new ArrayList<>(bullets.size());
        for (BulletState bullet : bullets) {
            bulletsPayload.add(Map.of(
                    "id", bullet.id(),
                    "x", (int) Math.round(bullet.x()),
                    "y", (int) Math.round(bullet.y()),
                    "size", BULLET_SIZE
            ));
        }
        payload.put("bullets", bulletsPayload);

        // Asteroids
        List<Map<String, Object>> asteroidsPayload = new ArrayList<>(asteroids.size());
        for (AsteroidState asteroid : asteroids) {
            asteroidsPayload.add(Map.of(
                    "id", asteroid.id(),
                    "x", (int) Math.round(asteroid.x()),
                    "y", (int) Math.round(asteroid.y()),
                    "size", asteroid.size()
            ));
        }
        payload.put("asteroids", asteroidsPayload);

        // Game State (Score, Lives, etc.)
        payload.put("score", sessionState.getScore());
        payload.put("lives", sessionState.getLives());
        payload.put("paused", sessionState.isPaused());
        payload.put("gameOver", sessionState.isGameOver());

        return payload;
    }

    public Map<String, Object> buildInitialStatePayload(double initialPlayerX) {
        Map<String, Object> payload = new HashMap<>();

        payload.put("width", GAME_WIDTH);
        payload.put("height", GAME_HEIGHT);

        Map<String, Object> player = new HashMap<>();
        player.put("x", (int) Math.round(initialPlayerX));
        player.put("y", PLAYER_Y);
        player.put("width", PLAYER_WIDTH);
        player.put("height", PLAYER_HEIGHT);
        player.put("speed", 320);
        payload.put("player", player);

        payload.put("asteroids", new Object[]{});
        payload.put("bullets", new Object[]{});
        payload.put("score", 0);
        payload.put("lives", 3);
        payload.put("paused", false);
        payload.put("gameOver", false);

        return payload;
    }
}

