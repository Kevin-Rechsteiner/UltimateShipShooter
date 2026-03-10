package dev.zwazel.game.game.engine;

import dev.zwazel.game.game.model.Bullet;
import dev.zwazel.game.game.model.Meteorite;
import dev.zwazel.game.game.model.Ship;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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

}
