package dev.zwazel.game.game.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    @Setter
    private Integer x;

    @Setter
    private Integer y;

    public Position(int x, int y) {
        this.x = 0;
        this.y = 0;

    }
}
