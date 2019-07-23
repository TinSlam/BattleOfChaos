package com.tinslam.battleheart.entities.projectiles;

import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.shapes.Rectangle;

import java.util.ArrayList;

/**
 * A class that contains all the existing arrows.
 */
public class Arrow extends Projectile{
    private static final Object arrowsLock = new Object();
    private static ArrayList<Arrow> arrows = new ArrayList<>();

    private Unit unit;
    private Rectangle rectangle;
    private Unit target;

    /**
     * Constructor.
     *  @param x The x position.
     * @param y The y position.
     */
    public Arrow(float x, float y, Unit target, Unit unit) {
        super(x, y, target.getCollisionBox().centerX(), target.getCollisionBox().centerY());
        if(unit == null){
            destroy();
            return;
        }
        this.target = target;
        this.unit = unit;
        setSpeed(unit.getProjectileSpeed());
        setDamage(unit.getDamage());
        setAnimation(new PerpetualAnimation(AnimationLoader.arrow, (long) (200 / getSpeed()), -1, 0, 0, this));
        rectangle = new Rectangle((int) x, (int) y, getAnimation().getWidth(), getAnimation().getHeight(), 0);

        float angle = (float) Math.atan2(getY2() - getY(), getX2() - getX());
        rectangle.setAngle((float) Math.toDegrees(-angle));

        addArrow(this);
    }

    /**
     * Is called when hits the target.
     */
    @Override
    public void hit() {
        if(!target.doesExist()) return;
        target.damage(getDamage(), unit);
    }

    /**
     * Is called when destroyed.
     */
    @Override
    public void destroyProjectile() {
        removeArrow(this);
    }

    /**
     * Adds the arrow to the arrows list.
     */
    private static void addArrow(final Arrow arrow){
        new Event() {
            @Override
            public void performAction() {
                    arrows.add(arrow);
            }
        };
    }

    /**
     * Removes the arrow from the arrows list.
     */
    private static void removeArrow(final Arrow arrow){
        new Event() {
            @Override
            public void performAction() {
                    arrows.remove(arrow);
            }
        };
    }

    /**
     * Is called on every frame.
     */
    @Override
    public void tickProjectile() {
        setX2(target.getCollisionBox().centerX());
        setY2(target.getCollisionBox().centerY());

        if(Utils.distance(getX(), getY(), getX2(), getY2()) < getSpeed() * GameView.density()){
            hit();
            destroy();
            return;
        }

        float angle = (float) Math.atan2(getY2() - getY(), getX2() - getX());
        setX((float) (getX() + Math.cos(angle) * getSpeed() * GameView.density()));
        setY((float) (getY() + Math.sin(angle) * getSpeed() * GameView.density()));
        if(rectangle != null) rectangle.setAngle((float) Math.toDegrees(-angle));
    }

    /**
     * Draws the arrow.
     */
    @Override
    public void renderProjectile(float xOffset, float yOffset) {
        if(getAnimation() != null && rectangle != null) getAnimation().render(rectangle.getMatrixInfo(), xOffset, yOffset);
    }

    /**
     * Updates the collision box of the entity. Setting it the same as the image. Must be overrode for more accurate hit boxes.
     */
    @Override
    public void updateCollisionBox() {

    }

    /**
     * Sets the x position.
     */
    @Override
    public boolean setX(float x){
        super.setX(x);

        if(rectangle != null) rectangle.position((int) getX(), (int) getY());
        return true;
    }

    /**
     * Sets the y position.
     */
    @Override
    public boolean setY(float y){
        super.setY(y);

        if(rectangle != null) rectangle.position((int) getX(), (int) getY());
        return true;
    }
}
