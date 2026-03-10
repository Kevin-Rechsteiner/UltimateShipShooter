package dev.zwazel.game.game.model;


import lombok.Getter;

public class Meteorite extends Entity {


    @Getter
    private Integer speed;
    @Getter
    private Integer health;
    private Velocity velocity;


    public Meteorite(Position position, Integer width, Integer height, Integer speed, Integer health, Boolean isActive) {
        super(position, width, height, isActive);
        this.speed = speed;
        this.health = health;
        this.velocity = new Velocity(speed, 0);


    }
    public void updatePosition() {
        this.position.setX((int) (position.getX() + velocity.getVx()));
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


    @Override
    public void update(double deltaTime) {
        this.position.setX((velocity.getVx() + deltaTime))

    }
}