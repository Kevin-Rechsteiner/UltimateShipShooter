package dev.zwazel.game.game.websocket;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
        Map<String, Integer> bulletHitsPerAsteroidId = new HashMap<>();
        Set<String> shipHitAsteroidIds = new HashSet<>();
        int shipHitCount = 0;


        for (BulletState bullet : bullets) {
            for (AsteroidState asteroid : asteroids) {
                if (isOverlapping(
                        bullet.x(), bullet.y(), BULLET_SIZE, BULLET_SIZE,
                        asteroid.x(), asteroid.y(), asteroid.size(), asteroid.size()
                )) {
                    hitBulletIds.add(bullet.id());
                    bulletHitsPerAsteroidId.merge(asteroid.id(), 1, Integer::sum);
                    break;
                }
            }
        }


        for (AsteroidState asteroid : asteroids) {
            if (isOverlapping(
                    playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT,
                    asteroid.x(), asteroid.y(), asteroid.size(), asteroid.size()
            )) {
                shipHitAsteroidIds.add(asteroid.id());
                shipHitCount++;
            }
        }

        return new CollisionResult(hitBulletIds, bulletHitsPerAsteroidId, shipHitAsteroidIds, shipHitCount);
    }

    private boolean isOverlapping(double ax, double ay, double aw, double ah,
                                   double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    public static class CollisionResult {
        public final Set<String> hitBulletIds;
        public final Map<String, Integer> bulletHitsPerAsteroidId;
        public final Set<String> shipHitAsteroidIds;
        public final int shipHitCount;

        public CollisionResult(
                Set<String> hitBulletIds,
                Map<String, Integer> bulletHitsPerAsteroidId,
                Set<String> shipHitAsteroidIds,
                int shipHitCount
        ) {
            this.hitBulletIds = hitBulletIds;
            this.bulletHitsPerAsteroidId = bulletHitsPerAsteroidId;
            this.shipHitAsteroidIds = shipHitAsteroidIds;
            this.shipHitCount = shipHitCount;
        }
    }
}

