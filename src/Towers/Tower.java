package Towers;

import Data.Enemy;
import Data.Entity;
import Graphics.Tile;
import Helpers.Clock;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static Helpers.Artist.TILE_SIZE;
import static Helpers.Artist.drawQuadTexture;
import static Helpers.Artist.drawQuadTextureRotation;

/**
 * Created by shurik on 11.05.2017.
 */
public abstract class Tower implements Entity {

    private float x, y, timeSinceLastShot, range, firingRate, angle;
    private int width, height, cost;
    private CopyOnWriteArrayList<Enemy> enemies;
    private boolean targeted, working;
    public Enemy target;
    private Texture[] textures;
    public ArrayList<Projectile> projectiles;
    public TowerType type;

    public Tower(TowerType type, Tile startTile, CopyOnWriteArrayList<Enemy> enemies) {
        this.type = type;
        this.textures = type.textures;
        this.x = startTile.getX();
        this.y = startTile.getY();
        this.width = startTile.getWidth();
        this.height = startTile.getHeight();
        this.cost = type.cost;
        this.range = type.range;
        this.firingRate = type.firingRate;
        this.targeted = false;
        this.working = true;
        this.timeSinceLastShot = 0;
        this.angle = 0f;
        this.enemies = enemies;
        this.projectiles = new ArrayList<Projectile>();
    }

    public Tower(TowerType type, CopyOnWriteArrayList<Enemy> enemies) {
        this.type = type;
        this.textures = type.textures;
        this.width = TILE_SIZE;
        this.height = TILE_SIZE;
        this.cost = type.cost;
        this.range = type.range;
        this.firingRate = type.firingRate;
        this.targeted = false;
        this.working = true;
        this.timeSinceLastShot = 0;
        this.angle = 0f;
        this.enemies = enemies;
        this.projectiles = new ArrayList<Projectile>();
    }

    private Enemy acquireTarget() {
        Enemy closest = null;
        float closestDistance = 3000;
        for (Enemy e : enemies) {
            if (isInRange(e) && findDistance(e) < closestDistance && e.isAlive()) {
                closestDistance = findDistance(e);
                closest = e;
            }
        }
        if (closest != null) targeted = true;
        return closest;
    }

    private boolean isInRange(Enemy e) {
        float xDistance = Math.abs(e.getX() + e.getWidth() / 2 - (x + TILE_SIZE / 2));
        float yDistance = Math.abs(e.getY() + e.getHeight() / 2 - (y + TILE_SIZE / 2));
        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2)) < range + TILE_SIZE / 2;
    }

    private float findDistance(Enemy e) {
        float xDistance = Math.abs(e.getX() - x);
        float yDistance = Math.abs(e.getY() - y);
        return (float) Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }

    private float calculateAngle() {
        double angleTemp = Math.atan2(target.getY() - y, target.getX() - x);
        return (float) Math.toDegrees(angleTemp) - 90;
    }

    public abstract void shoot(Enemy target);

    public void updateEnemyList(CopyOnWriteArrayList<Enemy> newList) {
        enemies = newList;
    }


    public void update() {
        if (working) {
            if (!targeted) {
                target = acquireTarget();
            } else {
                angle = calculateAngle();
                if (timeSinceLastShot > firingRate && isInRange(target)) {
                    shoot(target);
                    timeSinceLastShot = 0;
                }
            }

            if (target == null || !target.isAlive() || !isInRange(target)) targeted = false;

            timeSinceLastShot += Clock.INSTANCE.delta();

            for (Projectile p : projectiles) p.update();
            if (target != null && !target.isAlive() && type != TowerType.Mortal) projectiles.clear();

            draw();
        }
    }

    public void draw() {
        drawQuadTexture(textures[0], x, y, width, height);
        if (textures.length > 1)
            for (int i = 1; i < textures.length; i++) {
                drawQuadTextureRotation(textures[i], x, y, width, height, angle);
            }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Texture[] getTextures() {
        return textures;
    }

    public float getAngle() {
        return angle;
    }

    public Enemy getTarget() {
        return target;
    }

    public CopyOnWriteArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean state) {
        this.working = state;
    }

    public int getCost() {
        return cost;
    }

    public float getRange() {
        return range;
    }
}