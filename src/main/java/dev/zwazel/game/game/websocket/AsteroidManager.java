package dev.zwazel.game.game.websocket;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class AsteroidManager {
    private static final long ASTEROID_SPAWN_COOLDOWN_MILLIS = 900;
    private static final double ASTEROID_SPEED_PX_PER_SECOND = 170.0;
    private static final int[] ASTEROID_SIZES = new int[]{24, 32, 44};
    private static final int GAME_WIDTH = 720;
    private static final int GAME_HEIGHT = 520;

    private int hpForSize(int asteroidSize) {
        if (asteroidSize <= 24) return 1;
        if (asteroidSize <= 32) return 2;
        return 3;
    }

    public void updateAsteroids(
            List<AsteroidState> asteroids,
            long lastSpawnMillis,
            long nowMillis,
            double deltaSeconds
    ) {

        if ((nowMillis - lastSpawnMillis) >= ASTEROID_SPAWN_COOLDOWN_MILLIS) {
            spawnAsteroid(asteroids);
        }


        for (int i = asteroids.size() - 1; i >= 0; i--) {
            AsteroidState asteroid = asteroids.get(i);
            double nextY = asteroid.y() + ASTEROID_SPEED_PX_PER_SECOND * deltaSeconds;
            if (nextY > GAME_HEIGHT) {
                asteroids.remove(i);
            } else {
                asteroids.set(i, new AsteroidState(asteroid.id(), asteroid.x(), nextY, asteroid.size(), asteroid.hp()));
            }
        }
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
        asteroids.add(new AsteroidState(
                UUID.randomUUID().toString(),
                asteroidX,
                -asteroidSize,
                asteroidSize,
                hpForSize(asteroidSize)
        ));
    }
}
