package dev.zwazel.springintro;


import dev.zwazel.springintro.model.Position;
import jakarta.persistence.Entity;
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


    public moveLeft() {
        position.setX(position.getX() - 1);
        return position;
    }
    public moveRight() {
        position.setX(position.getX() + 1);
        return position;
    }
     public boolean canShoot() {
         return System.currentTimeMillis() - lastShotTime >= shootCooldown;
     }

     public void shoot() {
         if (canShoot()) {

             lastShotTime = System.currentTimeMillis();
         }
     }

     public void takeDamage(int damage) {
         health -= damage;
         if (health < 0) {
             health = 0;
         }
     }
     public isAlive() {
         return health > 0;
     }
}
