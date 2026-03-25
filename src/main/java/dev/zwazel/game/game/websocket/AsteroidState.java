package dev.zwazel.game.game.websocket;

/**
 * Repräsentiert einen Asteroiden im Spiel.
 */
public class AsteroidState {
    private String id;
    private double x;
    private double y;
    private int size;

    public AsteroidState(String id, double x, double y, int size) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public String id() { return id; }
    public double x() { return x; }
    public double y() { return y; }
    public int size() { return size; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}

