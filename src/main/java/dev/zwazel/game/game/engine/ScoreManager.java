package dev.zwazel.game.game.engine;

import dev.zwazel.game.game.model.Meteorite;

public class ScoreManager {
    private Integer currentScore;
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
