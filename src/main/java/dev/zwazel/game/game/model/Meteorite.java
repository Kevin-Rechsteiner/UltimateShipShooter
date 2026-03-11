package dev.zwazel.game.game.model;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Meteorite extends Entity {



    private Integer speed;

    private Integer health;
    private Velocity velocity;
    private static Integer size;

    public Meteorite(Position position, Integer width, Integer height, Integer speed, Integer health, Boolean isActive, Integer size) {
        super(position, width, height, isActive);
        this.speed = speed;
        this.health = health;
        this.velocity = new Velocity(speed, 0);
        this.size = size;


    }
    public void updatePosition() {
        this.position.setX((int) (position.getX() + velocity.getVx()));
        position.setY((int) (position.getY() + velocity.getVy()));
    }
    public void takeDamage(int damage) {
        health -= damage;
    }

    public Boolean isAlive() {
        if (health <= 0) {
            return false;
        }
        else {
            return true;
    }}
    public static Integer getSize() {
        return size;

    }

    public Integer getHealth(
    ) {
        return health;


    }

    public void update(double deltaTime) {
        position.setX((int) (position.getX() + velocity.getVx() * deltaTime));

    }
}