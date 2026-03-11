package dev.zwazel.game.game.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;

import java.awt.*;

@Getter
public abstract class Entity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public Position position;
    public Integer width;
    public Integer height;
    public Boolean isActive;

     public Entity(Position position, Integer width, Integer height, Boolean isActive) {
         this.position = position;
         this.width = width;
         this.height = height;
         this.isActive = isActive;
     }
    public abstract void update(double deltaTime);

    public Position setPosition(Position position) {
            this.position = position;
            return position;
     }

    public Integer setWidth(Integer width) {
         this.width = width;
         return width;
     }

    public Integer setHeight(Integer height) {
            this.height = height;
            return height;
     }

    public void setIsActive(Boolean isActive) {
         this.isActive = isActive;
     }
     public void isOnScreen(Integer width, Integer height) {
        if (position.getX() < width | position.getX() > width) {
            isActive = false;
        }

     }
     public Rectangle getBoundingBox() {
            return new Rectangle(position.getX(), position.getY(), width, height);
     }
}
