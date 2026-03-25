package dev.zwazel.game.game.entities;


import dev.zwazel.game.game.util.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Ship extends Entity {

    private Position position;
    private int health;
    private int maxHealth;
    private Integer width;
    private Integer height;
    private double lastShotTime;
    private double shootCooldown;



    public Ship(Position position, int health, int maxHealth, int width, int height, long shootCooldown, Boolean isActive) {
        super(position, width, height, isActive);

        this.position = position;
        this.health = health;
        this.maxHealth = maxHealth;
        this.width = width;
        this.height = height;
        this.shootCooldown = shootCooldown;
    }
    public Position moveLeft() {
        if (-150 < position.getX()){
            position.setX(position.getX() - 1);
            return position;
    }
    return position;}
    public Position moveRight() {
        if (150 > position.getX()){
        position.setX(position.getX() + 1);
        return position;
    }
    return position;}


    public boolean canShoot() {
         return System.currentTimeMillis() - lastShotTime >= shootCooldown;
     }



     public void takeDamage(int damage) {
         health -= damage;
         if (health < 0) {
             health = 0;
         }
     }
     public Boolean isAlive() {
         return health > 0;
     }


    public void update(double deltaTime) {
        shootCooldown += deltaTime;
    }
}
