package dev.zwazel.springintro;


public class Meteorite {

    private int x;
    private int y;
    private int size;
    private int speed;

    public Meteorite(int screenWidth, int screenHeight) {
        this.x = (int) (Math.random() * screenWidth);
        this.y = 0;
        this.size = (int) (Math.random() * 50) + 10;
        this.speed = (int) (Math.random() * 5) + 1;
    }

    public void move() {
        this.y += speed;
    }


}