package dev.zwazel.game.game.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Verwaltet Bullet-Logik: Spawn mit Cooldown, Bewegung, Despawn.
 */
public class BulletManager {
    private static final int BULLET_SIZE = 9;
    private static final double BULLET_SPEED_PX_PER_SECOND = 520.0;
    private static final long SHOOT_COOLDOWN_MILLIS = 180;
    private static final int PLAYER_WIDTH = 52;
    private static final int PLAYER_Y = 520 - 30 - 20; // GAME_HEIGHT - PLAYER_HEIGHT - 20

    public void updateBullets(
            List<BulletState> bullets,
            double playerX,
            boolean shootPressed,
            long lastShotMillis,
            long nowMillis,
            double deltaSeconds
    ) {
        // Spawn bei Cooldown
        if (shootPressed && (nowMillis - lastShotMillis) >= SHOOT_COOLDOWN_MILLIS) {
            double bulletX = playerX + (PLAYER_WIDTH / 2.0) - (BULLET_SIZE / 2.0);
            double bulletY = PLAYER_Y - BULLET_SIZE;
            bullets.add(new BulletState(UUID.randomUUID().toString(), bulletX, bulletY));
        }

        // Bewegung + Despawn
        List<BulletState> toRemove = new ArrayList<>();
        for (BulletState bullet : bullets) {
            double nextY = bullet.y() - BULLET_SPEED_PX_PER_SECOND * deltaSeconds;
            if (nextY + BULLET_SIZE < 0) {
                toRemove.add(bullet);
            } else {
                bullet.setY(nextY);
            }
        }
        bullets.removeAll(toRemove);
    }

    public long getLastShotMillis(boolean shootPressed, long currentLastShotMillis, long nowMillis) {
        if (shootPressed && (nowMillis - currentLastShotMillis) >= SHOOT_COOLDOWN_MILLIS) {
            return nowMillis;
        }
        return currentLastShotMillis;
    }

    public int getBulletSize() {
        return BULLET_SIZE;
    }
}

