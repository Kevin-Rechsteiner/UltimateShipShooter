package dev.zwazel.game.game.engine;
import dev.zwazel.game.game.engine.EntityManager;
import dev.zwazel.game.game.model.Meteorite;
import dev.zwazel.game.game.model.Ship;
import dev.zwazel.game.game.engine.CollisionEvent;
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
                if (CollisionEvent.checkCollision(ship, meteorite) == true);
                {
                    EntityManager.removeMeteorite(meteorite);

                }

            }
        }
    }

}
