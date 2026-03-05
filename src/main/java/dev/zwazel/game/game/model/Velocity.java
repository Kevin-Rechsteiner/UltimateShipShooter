package dev.zwazel.game.game.model;

public class Velocity {
    private Integer x;
    private Integer y;

    public Velocity(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public double getVx() {
        return x;
    }

    public void setVx(Integer x) {
        this.x = x;
    }

    public double getVy() {
        return y;
    }

    public void setVy(Integer y) {
        this.y = y;
    }
}
