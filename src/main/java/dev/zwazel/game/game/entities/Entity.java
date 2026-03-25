package dev.zwazel.game.game.entities;

import dev.zwazel.game.game.util.Position;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;

import java.awt.*;

@Getter
public abstract class Entity {
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

    public void setPosition(Position position) {
            this.position = position;
     }

    public void setWidth(Integer width) {
         this.width = width;
     }

    public void setHeight(Integer height) {
            this.height = height;
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
