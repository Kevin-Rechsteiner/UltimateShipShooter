package dev.zwazel.game.game.websocket;

import java.util.ArrayList;
import java.util.List;
public class GameSessionState {
    private double playerX;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean shootPressed;
    private long lastShotMillis;
    private List<BulletState> bullets;
    private List<AsteroidState> asteroids;
    private long lastAsteroidSpawnMillis;
    private int lives;
    private int score;
    private int highScore;
    private boolean paused;

    public GameSessionState(
            double playerX,
            int initialLives,
            int initialScore
    ) {
        this.playerX = playerX;
        this.leftPressed = false;
        this.rightPressed = false;
        this.shootPressed = false;
        this.lastShotMillis = 0L;
        this.bullets = new ArrayList<>();
        this.asteroids = new ArrayList<>();
        this.lastAsteroidSpawnMillis = 0L;
        this.lives = initialLives;
        this.score = initialScore;
        this.highScore = initialScore;
        this.paused = false;
    }

    public double getPlayerX() { return playerX; }
    public void setPlayerX(double playerX) { this.playerX = playerX; }

    public boolean isLeftPressed() { return leftPressed; }
    public void setLeftPressed(boolean leftPressed) { this.leftPressed = leftPressed; }

    public boolean isRightPressed() { return rightPressed; }
    public void setRightPressed(boolean rightPressed) { this.rightPressed = rightPressed; }

    public boolean isShootPressed() { return shootPressed; }
    public void setShootPressed(boolean shootPressed) { this.shootPressed = shootPressed; }

    public long getLastShotMillis() { return lastShotMillis; }
    public void setLastShotMillis(long lastShotMillis) { this.lastShotMillis = lastShotMillis; }

    public List<BulletState> getBullets() { return bullets; }
    public void setBullets(List<BulletState> bullets) { this.bullets = bullets; }

    public List<AsteroidState> getAsteroids() { return asteroids; }
    public void setAsteroids(List<AsteroidState> asteroids) { this.asteroids = asteroids; }

    public long getLastAsteroidSpawnMillis() { return lastAsteroidSpawnMillis; }
    public void setLastAsteroidSpawnMillis(long lastAsteroidSpawnMillis) { this.lastAsteroidSpawnMillis = lastAsteroidSpawnMillis; }

    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }

    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }


    public boolean isGameOver() { return lives <= 0; }
}

