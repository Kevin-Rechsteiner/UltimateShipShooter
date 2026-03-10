package dev.zwazel.game.game.model;

public class Entity {
    private Position position;
    private Integer width;
    private Integer height;
    private Boolean isActive;

     public Entity(Position position, Integer width, Integer height, Boolean isActive) {
         this.position = position;
         this.width = width;
         this.height = height;
         this.isActive = isActive;
     }

     public Position getPosition() {
         return position;
     }
     public Position setPosition(Position position) {
            this.position = position;
            return position;
     }

     public Integer getWidth() {
         return width;
     }
     public Integer setWidth(Integer width) {
         this.width = width;
         return width;
     }

     public Integer getHeight() {
         return height;
     }

     public Integer setHeight(Integer height) {
            this.height = height;
            return height;
     }

     public Boolean getIsActive() {
         return isActive;
     }

     public void setIsActive(Boolean isActive) {
         this.isActive = isActive;
     }
}
