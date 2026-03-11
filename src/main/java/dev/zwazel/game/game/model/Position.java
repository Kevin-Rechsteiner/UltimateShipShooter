package dev.zwazel.game.game.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    private Integer x;
    private Integer y;

    public Position(int x, int y) {
        this.x = 0;
        this.y = 0;

    }


}
