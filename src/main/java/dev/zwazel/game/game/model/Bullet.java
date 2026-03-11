package dev.zwazel.game.game.model;

public class Bullet {
    private Integer damage;

    private Velocity velocity;
    private Double timeAlive;
    private Double maxLifetime;
    private Position position;

    public Bullet(Position startPosition, Velocity velocity, Double timeAlive) {
        position = startPosition;
        this.velocity = velocity;
        this.timeAlive = timeAlive;



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
    public void setDamage(Integer damage) {
        this.damage = damage;
    }
    public Boolean isAlive() {
        if (timeAlive >= maxLifetime) {
            return false;
        }
        else {
            return true;
        }
    }
    public  Double getTimeAlive() {
        return timeAlive;
    }
    public Double getMaxLifetime() {
            return maxLifetime;
        }
        public void update(double deltaTime) {
            position.setY((int) (position.getY() + velocity.getVy() * deltaTime));
            timeAlive += deltaTime;
        }


}

