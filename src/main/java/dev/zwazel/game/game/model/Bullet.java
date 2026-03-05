package dev.zwazel.game.game.model;

public class Bullet {
    private Integer damage;

    private Velocity velocity;
    private Long timeAlive;
    private Long maxLifetime;
    private Position position;

    public Bullet(Position startPosition) {
        position = startPosition;
        velocity = new Velocity(0, -10);
        timeAlive = 0L;


        this.timeAlive = System.currentTimeMillis();
    }

    public Integer getDamage() {
        return damage;
    }
    public Velocity getVelocity() {
        return velocity;
    }
    public Position getPosition() {
        return position;
    }
    public void updatePosition() {
        position.setY((int) (position.getY() + velocity.getVy()));
        // Add  frame time to time alive idk how

}
    public  Long getTimeAlive() {
        return timeAlive;
    }
        public Long getMaxLifetime() {
            return maxLifetime;
        }
}
