package dev.zwazel.game.game.entities;

import dev.zwazel.game.game.util.Position;
import dev.zwazel.game.game.util.Velocity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bullet extends Entity {
    private Integer damage;
    private Velocity velocity;
    private Double timeAlive;
    private Double maxLifetime;

    public Bullet(Position startPosition, Velocity velocity, Double timeAlive, Long id, Integer width, Integer height, Boolean isActive, Double maxLifetime) {
        super(startPosition, width, height, isActive);
        this.velocity = velocity;
        this.timeAlive = timeAlive;
    }

    public void updatePosition() {
        position.setY((int) (position.getY() + velocity.getVy()));
    }

    public Boolean isAlive() {
        return timeAlive < maxLifetime;
    }

    public void update(double deltaTime) {
        position.setY((int) (position.getY() + velocity.getVy() * deltaTime));
        timeAlive += deltaTime;
    }


}

