package dev.zwazel.game.game.managers;

import dev.zwazel.game.game.entities.Meteorite;
import dev.zwazel.game.game.util.AsteroidSizes;

public class ScoreManager {
    private Integer currentScore = 0;
    private Integer meteoritesDestroyed;


    public void addScore(Integer score) {
        currentScore += score;
    }
    public void meteoritesDestroyed(Meteorite meteoritesDestroyed) {
        AsteroidSizes size = meteoritesDestroyed.getSize();
        switch (size) {
            case SMALL -> {
                addScore(20);
            }
            case MEDIUM -> {
                addScore(40);
            }
            case BIG -> {
                addScore(60);
            }
        }

    }

}
