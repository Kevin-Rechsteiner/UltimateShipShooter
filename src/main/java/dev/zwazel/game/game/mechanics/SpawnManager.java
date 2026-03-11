package dev.zwazel.game.game.mechanics;

import dev.zwazel.game.game.entities.Meteorite;
import dev.zwazel.game.game.managers.EntityManager;
import dev.zwazel.game.game.util.GameConstants;
import dev.zwazel.game.game.util.Position;

import java.util.Random;

import static dev.zwazel.game.game.util.GameConstants.meteoriteSpeed;


public class SpawnManager {
    private EntityManager entityManager;
    private Double TimeSinceLastSpawn;
    private Double SpawnInterval;
    public SpawnManager(EntityManager entityManager, Double spawnInterval) {
        this.entityManager = entityManager;
        this.SpawnInterval = spawnInterval;
        this.TimeSinceLastSpawn = 0.0;
    }

        public void update(Double deltaTime) {
            TimeSinceLastSpawn += deltaTime;
            if (TimeSinceLastSpawn >= SpawnInterval) {
                spawnMeteorite();
                TimeSinceLastSpawn = 0.0;
            }

        }

        public void spawnMeteorite() {
            Random randomX = new Random();
            int X = randomX.nextInt(401);
            Random randomSize = new Random();
            int size = randomSize.nextInt(4);
            if (size == 1) {
                Integer width = 20;
                Integer height = 20;
                Integer MeteoriteHealth = 2;
            }
            if (size == 2) {
                Integer width = 30;
                Integer height = 30;
                Integer MeteoriteHealth = 3;
            }
            if (size == 3) {
                Integer width = 40;
                Integer height = 40;
                Integer MeteoriteHealth = 4;
            }
            else {
                Integer width = 10;
                Integer height = 10;
                Integer MeteoriteHealth = 1;

            }
            Position position = new Position(X, 0);
            Meteorite meteorite = new Meteorite(position, width, height, meteoriteSpeed, MeteoriteHealth, true, size);

               }
}
