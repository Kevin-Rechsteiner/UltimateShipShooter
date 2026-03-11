package dev.zwazel.game.game.model;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Meteorite extends Entity {



    private Integer speed;

    private Integer health;
    private Velocity velocity;


    public Meteorite(Position position, Integer width, Integer height, Integer speed, Integer health, Boolean isActive) {
        super(position, width, height, isActive);
        this.speed = speed;
        this.health = health;
        this.velocity = new Velocity(speed, 0);


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

    public Integer getHealth(
    ) {
        return health;


    }

    public void update(double deltaTime) {
        position.setX((int) (position.getX() + velocity.getVx() * deltaTime));

    }
}