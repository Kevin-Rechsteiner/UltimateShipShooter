package dev.zwazel.game.game.managers;

import dev.zwazel.game.game.entities.Entity;

import java.awt.*;

public class CollisionEvent {
    private Entity firstEntity;
    private Entity secondEntity;

    public CollisionEvent(Entity firstEntity, Entity secondEntity) {
        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
    }

    public static Boolean checkCollision(Entity firstEntity, Entity secondEntity) {
        Rectangle hitbox1 = firstEntity.getBoundingBox();
        Rectangle hitbox2 = secondEntity.getBoundingBox();
        return hitbox1.intersects(hitbox2);
    }



}
