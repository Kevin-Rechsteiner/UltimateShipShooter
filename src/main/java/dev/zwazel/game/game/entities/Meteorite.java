package dev.zwazel.game.game.entities;


import dev.zwazel.game.game.util.Position;
import dev.zwazel.game.game.util.Velocity;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Meteorite extends Entity {



    private Integer speed;

    private Integer health;
    private Velocity velocity;
    private Integer size;

    public Meteorite(Position position, Integer width, Integer height, Integer speed, Integer health, Boolean isActive, Integer size) {
        super(position, width, height, isActive);
        this.speed = speed;
        this.health = health;
        this.velocity = new Velocity(speed, 0);
        this.size = size;


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

    public void update(double deltaTime) {
        position.setX((int) (position.getX() + velocity.getVx() * deltaTime));
        position.setY((int) (position.getY() + velocity.getVy() * deltaTime));
    }
}