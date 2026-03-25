package dev.zwazel.game.game.websocket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Prüft Kollisionen: Bullet-Asteroid und Ship-Asteroid.
 * Berechnet Score und Leben-Verlust.
 */
public class CollisionDetector {
    private static final int BULLET_SIZE = 9;
    private static final int PLAYER_WIDTH = 52;
    private static final int PLAYER_HEIGHT = 30;
    private static final int PLAYER_Y = 520 - 30 - 20;

    public CollisionResult detectCollisions(
            List<BulletState> bullets,
            List<AsteroidState> asteroids,
            double playerX
    ) {
        Set<String> hitBulletIds = new HashSet<>();
        Set<String> hitAsteroidIds = new HashSet<>();
        int scoreGain = 0;
        int shipHitCount = 0;

        // Bullet-Asteroid Kollisionen
        for (BulletState bullet : bullets) {
            for (AsteroidState asteroid : asteroids) {
                if (isOverlapping(
                        bullet.x(), bullet.y(), BULLET_SIZE, BULLET_SIZE,
                        asteroid.x(), asteroid.y(), asteroid.size(), asteroid.size()
                )) {
                    hitBulletIds.add(bullet.id());
                    hitAsteroidIds.add(asteroid.id());
                    scoreGain += scoreForAsteroidSize(asteroid.size());
                }
            }
        }

        // Ship-Asteroid Kollisionen
        for (AsteroidState asteroid : asteroids) {
            if (isOverlapping(
                    playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT,
                    asteroid.x(), asteroid.y(), asteroid.size(), asteroid.size()
            )) {
                hitAsteroidIds.add(asteroid.id());
                shipHitCount++;
            }
        }

        return new CollisionResult(hitBulletIds, hitAsteroidIds, scoreGain, shipHitCount);
    }

    private boolean isOverlapping(double ax, double ay, double aw, double ah,
                                   double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    private int scoreForAsteroidSize(int size) {
        if (size <= 24) return 30;
        if (size <= 32) return 20;
        return 10;
    }

    public static class CollisionResult {
        public final Set<String> hitBulletIds;
        public final Set<String> hitAsteroidIds;
        public final int scoreGain;
        public final int shipHitCount;

        public CollisionResult(
                Set<String> hitBulletIds,
                Set<String> hitAsteroidIds,
                int scoreGain,
                int shipHitCount
        ) {
            this.hitBulletIds = hitBulletIds;
            this.hitAsteroidIds = hitAsteroidIds;
            this.scoreGain = scoreGain;
            this.shipHitCount = shipHitCount;
        }
    }
}

