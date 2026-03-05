package dev.zwazel.game.game.engine;

import dev.zwazel.game.game.model.Bullet;
import dev.zwazel.game.game.model.Meteorite;
import dev.zwazel.game.game.model.Ship;

import java.util.List;

public class EntityManager {
    private List<Ship> ships;
    private List<Bullet> bullets;
    private List<Meteorite> meteorites;


    public void addShip(Ship ship) {
        ships.add(ship);
    }
    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }
    public void addMeteorite(Meteorite meteorite) {
        meteorites.add(meteorite);
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
    public List<Meteorite> getMeteorites() {
        return meteorites;
    }
    public List<Ship> getShips() {
        return ships;
    }
}
