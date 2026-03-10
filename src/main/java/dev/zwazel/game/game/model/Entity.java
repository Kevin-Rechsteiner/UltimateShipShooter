package dev.zwazel.game.game.model;

import lombok.Getter;

@Getter
public abstract class Entity {
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
}
