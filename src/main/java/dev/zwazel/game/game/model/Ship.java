package dev.zwazel.game.game.model;


import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Ship {
    @Id
    private Long id;
    private Position position;
    private int health;
    private int maxHealth;
    private double width;
    private double height;
    private long lastShotTime;
    private long shootCooldown;


    //constructor
    public Ship(Position position, int health, int maxHealth, double width, double height, long shootCooldown) {
        Position startPosition = new Position(0 ,0);
        this.position = startPosition;
        this.health = 100;
        this.maxHealth = 150;;
        this.width = 150;
        this.height = 500;
        this.shootCooldown = 50;
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

     public void shoot() {
         if (canShoot()) {
             // create bullet
             lastShotTime = System.currentTimeMillis();
         }
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

}
