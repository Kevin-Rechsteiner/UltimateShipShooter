package dev.zwazel.game.game.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Verwaltet Asteroid-Logik: Random-Spawn, Bewegung nach unten.
 */
public class AsteroidManager {
    private static final long ASTEROID_SPAWN_COOLDOWN_MILLIS = 900;
    private static final double ASTEROID_SPEED_PX_PER_SECOND = 170.0;
    private static final int[] ASTEROID_SIZES = new int[]{24, 32, 44};
    private static final int GAME_WIDTH = 720;
    private static final int GAME_HEIGHT = 520;

    public void updateAsteroids(
            List<AsteroidState> asteroids,
            long lastSpawnMillis,
            long nowMillis,
            double deltaSeconds
    ) {
        // Spawn neuer Asteroiden bei Cooldown
        if ((nowMillis - lastSpawnMillis) >= ASTEROID_SPAWN_COOLDOWN_MILLIS) {
            spawnAsteroid(asteroids);
        }

        // Bewegung + Despawn außerhalb Spielfeld
        List<AsteroidState> toRemove = new ArrayList<>();
        for (AsteroidState asteroid : asteroids) {
            double nextY = asteroid.y() + ASTEROID_SPEED_PX_PER_SECOND * deltaSeconds;
            if (nextY > GAME_HEIGHT) {
                toRemove.add(asteroid);
            } else {
                asteroid.setY(nextY);
            }
        }
        asteroids.removeAll(toRemove);
    }

    public long getLastSpawnMillis(long lastSpawnMillis, long nowMillis) {
        if ((nowMillis - lastSpawnMillis) >= ASTEROID_SPAWN_COOLDOWN_MILLIS) {
            return nowMillis;
        }
        return lastSpawnMillis;
    }

    private void spawnAsteroid(List<AsteroidState> asteroids) {
        int asteroidSize = ASTEROID_SIZES[ThreadLocalRandom.current().nextInt(ASTEROID_SIZES.length)];
        double asteroidX = ThreadLocalRandom.current().nextDouble(0, GAME_WIDTH - asteroidSize);
        asteroids.add(new AsteroidState(UUID.randomUUID().toString(), asteroidX, -asteroidSize, asteroidSize));
    }
}

