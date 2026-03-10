package dev.zwazel.game.game.engine;

import dev.zwazel.game.game.model.Ship;

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
