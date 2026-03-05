package dev.zwazel.game.game.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    @Setter
    private Integer x;
    @Getter
    @Setter
    private Integer y;

    public Position(int x, int y) {
        this.x = 0;
        this.y = 0;

    }
    public Integer getX() {
        return this.x;
    }
    public Integer getY() {
        return this.y;
    }
}
