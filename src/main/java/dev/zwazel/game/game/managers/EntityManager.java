package dev.zwazel.game.game.managers;

import dev.zwazel.game.game.entities.Bullet;
import dev.zwazel.game.game.entities.Meteorite;
import dev.zwazel.game.game.entities.Ship;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EntityManager {
    //private static List<Ship> ships;
    //private static List<Bullet> bullets;
    //private static List<Meteorite> meteorites;


    public void addShip(Ship ship) {
        ships.add(ship);
    }
    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }
    public void addMeteorite(Meteorite meteorite) {
        meteorites.add(meteorite);
    }
    public void checkIfAliveMeteorite(Meteorite meteorite) {
        if (meteorite.isAlive() == false) {
        removeMeteorite(meteorite);
    }}
    public Ship getShip(int id) {
        for (Ship ship : ships) {
            if (ship.getId() == id) {
                return ship;
            }
        }
        return null;
    }

    public List<Meteorite> getMeteorites() {
        return meteorites;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<Ship> getShips() {
        return ships;
    }
    public static void removeMeteorite(Meteorite meteorite) {
            meteorites.remove(meteorite);

    }
    public static void removeBullet(Bullet bullet) {
        bullets.remove(bullet);
    }

    public void checkIfAliveShip(Ship ship) {
        if (ship.isAlive() == false) {
            removeShip(ship);
        }
    }
    public static void removeShip(Ship ship) {
        ships.remove(ship);
    }
    public void checkIfAliveBullet(Bullet bullet) {
        if (bullet.isAlive() == false) {
            removeBullet(bullet);
        }
    }
    public void update(double deltaTime) {
        for (Ship ship : ships) {
            ship.update(deltaTime);
        }

        for (Meteorite meteorite : meteorites) {
            meteorite.update(deltaTime);
        }

        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
        }
    }
    public void resetGame() {
        ships.clear();
        bullets.clear();
        meteorites.clear();
    }

}
