package dev.zwazel.game.game.engine;

import dev.zwazel.game.game.model.Position;

public class GameConstants {
    public static final Integer width = 800; // map width
    public static final Integer height = 600; // map height
    public static final Position playerStartPosition = new Position(0, 0);
    public static final Integer shipHeight = 50;
    public static final Integer shipWidth = 50;
    public static final Integer movementSpeedShip = 5;
    public static final Integer bulletPixelPerFrame = 10;
    public static final Integer bulletDamage = 1;
    public static final Integer projectlieLifetime = 2000; // milliseconds
    public static final Integer fireRate = 200; // milliseconds
    public static final Integer meteoriteSmallSize = 30;
    public static final Integer meteoriteMediumSize = 50;
    public static final Integer meteoriteLargeSize = 70;
    public static final Integer meteoriteSmallHealth = 1;
    public static final Integer meteoriteMediumHealth = 3;
    public static final Integer meteoriteLargeHealth = 5;

}
