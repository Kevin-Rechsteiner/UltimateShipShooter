package dev.zwazel.game.game.model;


import lombok.Getter;

public class Meteorite {

    private Position position;
    @Getter
    private Integer size;
    private Integer speed;
    @Getter
    private Integer health;
    private Velocity velocity;

    public Meteorite(Position position, Integer size, Integer speed, Integer health) {
        this.position = position;
        this.size = size;
        this.speed = speed;
        this.health = health;
        this.velocity = new Velocity(speed, 0);

    }
    public void updatePosition() {
        position.setX((int) (position.getX() + velocity.getVx()));
        position.setY((int) (position.getY() + velocity.getVy()));
    }
    public void takeDamage(int damage) {
        health -= damage;
    }
    public void checkOnScreen(Position position) {
        if (position.getX() < -150 || position.getX() > 150) {
            // Remove meteorite from game
        }
    }


}