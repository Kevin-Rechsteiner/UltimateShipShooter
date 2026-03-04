package dev.zwazel.springintro;


import dev.zwazel.springintro.model.Position;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Ship {
    @Id
    private Long id;
    private Position position;
    private int health;
    private int maxHealth;
    private int width;
    private int height;
    private long lastShotTime;
    private long shootCooldown;

}
