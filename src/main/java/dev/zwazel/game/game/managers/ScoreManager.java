package dev.zwazel.game.game.managers;

import dev.zwazel.game.game.entities.Meteorite;

public class ScoreManager {
    private Integer currentScore = 0;
    private Integer meteoritesDestroyed;


    public void addScore(Integer score) {
        currentScore += score;
    }
    public void meteoritesDestroyed(Meteorite meteoritesDestroyed) {
        Integer size = meteoritesDestroyed.getSize();
        if (size == 1) {
                addScore(20);
        }
        if (size == 2) {
            addScore(40);
        }
        if (size == 3) {
            addScore(60);
        }
    }

}
