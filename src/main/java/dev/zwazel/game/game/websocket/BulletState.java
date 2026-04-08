package dev.zwazel.game.game.websocket;

public class BulletState {
    private String id;
    private double x;
    private double y;

    public BulletState(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String id() { return id; }
    public double x() { return x; }
    public double y() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}

