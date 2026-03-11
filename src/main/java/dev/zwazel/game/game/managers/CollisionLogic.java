package dev.zwazel.game.game.managers;
import dev.zwazel.game.game.entities.Meteorite;
import dev.zwazel.game.game.entities.Ship;

import java.util.List;

public class CollisionLogic {
    private EntityManager entityManager;
    public CollisionLogic(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void checkCollisions() {
        List<Ship> ships = entityManager.getShips();
        List<Meteorite> meteorites = entityManager.getMeteorites();
        for (Ship ship : ships) {
            for (Meteorite meteorite : meteorites) {
                if (CollisionEvent.checkCollision(ship, meteorite))
                {
                    EntityManager.removeMeteorite(meteorite);

                }

            }
        }
    }

}
