package com.tinslam.battleheart.entities.projectiles;

import android.graphics.Canvas;

import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.states.State;

import java.util.ArrayList;

/**
 * A class that holds all the existing projectiles.
 */
public abstract class Projectile extends Entity {
    private static final Object projectilesLock = new Object();
    private static ArrayList<Projectile> projectiles = new ArrayList<>();

    private float x2, y2;
    private float speed = 3;
    private float damage = 10;

    /**
     * Constructor.
     *
     * @param x The x position.
     * @param y The y position.
     */
    Projectile(float x, float y, float x2, float y2) {
        super(x, y);

        this.x2 = x2;
        this.y2 = y2;

        addProjectile(this);
    }

    /**
     * Is called when target is hit.
     */
    public abstract void hit();

    /**
     * Is called when destroyed.
     */
    public abstract void destroyProjectile();

    /**
     * Is called on every frame.
     */
    public abstract void tickProjectile();

    /**
     * Draws the projectile.
     */
    public abstract void renderProjectile(float xOffset, float yOffset);

    /**
     * Is called when destroyed.
     */
    @Override
    public void destroyEntity() {
        destroyProjectile();
        removeProjectile(this);
    }

    /**
     * Is called on every frame.
     */
    @Override
    public void tickEntity() {
        tickProjectile();
    }

    /**
     * Draws the projectile.
     */
    @Override
    public void renderEntity(float xOffset, float yOffset) {
        renderProjectile(xOffset, yOffset);
    }

    /**
     * Adds the projectile to the projectiles list.
     */
    private static void addProjectile(final Projectile projectile){
        new Event() {
            @Override
            public void performAction() {
                    projectiles.add(projectile);
            }
        };
    }

    /**
     * Removes the projectile from the projectiles list.
     */
    private static void removeProjectile(final Projectile projectile){
        new Event() {
            @Override
            public void performAction() {
                    projectiles.remove(projectile);
            }
        };
    }

    /**
     * @return The target x position.
     */
    float getX2() {
        return x2;
    }

    /**
     * Sets the target x position.
     */
    public void setX2(float x2) {
        this.x2 = x2;
    }

    /**
     * @return The target y position.
     */
    float getY2() {
        return y2;
    }

    /**
     * Sets the target y position.
     */
    public void setY2(float y2) {
        this.y2 = y2;
    }

    /**
     * @return The speed.
     */
    public float getSpeed() {
        return speed * (State.slowMo ? 0.33f : 1);
    }

    /**
     * Sets the speed.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return The damage.
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Sets the damage.
     */
    public void setDamage(float damage) {
        this.damage = damage;
    }
}
