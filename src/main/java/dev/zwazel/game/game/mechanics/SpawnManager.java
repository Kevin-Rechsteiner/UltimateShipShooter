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
            int width, height, meteoriteHealth;
            switch (size) {
                case 1 -> {
                    width = 20;
                    height = 20;
                    meteoriteHealth = 2;
                }
                case 2 -> {
                    width = 30;
                    height = 30;
                    meteoriteHealth = 3;
                }
                case 3 -> {
                    width = 40;
                    height = 40;
                    meteoriteHealth = 4;
                }
                default -> {
                    width = 10;
                    height = 10;
                    meteoriteHealth = 1;
                }
            }
            Position position = new Position(X, 0);
            Meteorite meteorite = new Meteorite(position, width, height, meteoriteSpeed, meteoriteHealth, true, size);

               }
}
