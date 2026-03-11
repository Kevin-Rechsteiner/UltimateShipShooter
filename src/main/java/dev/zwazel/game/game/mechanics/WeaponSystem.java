package dev.zwazel.game.game.mechanics;

import dev.zwazel.game.game.entities.Ship;
import dev.zwazel.game.game.managers.EntityManager;

public class WeaponSystem {
    private EntityManager entityManager;
    private Ship ship;
    private long fireRate; // milliseconds
    public WeaponSystem(EntityManager entityManager, Ship ship, long fireRate) {
        this.entityManager = entityManager;
        this.ship = ship;
        this.fireRate = fireRate;
    }
}
