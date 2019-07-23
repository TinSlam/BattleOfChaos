package com.tinslam.battleheart.entities.projectiles;

import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.PCs.Archer;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.constants.SpellConsts;
import com.tinslam.battleheart.utils.shapes.Rectangle;

import java.util.ArrayList;

public class PowerShotArrow extends Projectile{
    private static final Object powerShotArrowsLock = new Object();
    private static ArrayList<PowerShotArrow> powerShotArrows = new ArrayList<>();

    private ArrayList<Unit> unitsHit = new ArrayList<>();
    private Archer archer;
    private Rectangle rectangle;

    /**
     * Constructor.
     *
     */
    public PowerShotArrow(Archer archer, float x2, float y2) {
        super(archer.getCollisionBox().centerX(), archer.getCollisionBox().centerY(), x2, y2);

        this.archer = archer;
        setSpeed(SpellConsts.POWERSHOT_SPEED);
        setDamage(SpellConsts.POWERSHOT_DAMAGE);
        setAnimation(new PerpetualAnimation(AnimationLoader.arrow, (long) (200 / getSpeed()), -1, 0, 0, this));
        rectangle = new Rectangle((int) (getX() + getAnimation().getWidth() / 2), (int) (getY() + getAnimation().getHeight() / 2), getAnimation().getWidth(), getAnimation().getHeight(),
                (float) Math.toDegrees(Math.atan2(-y2 + getY(), x2 - getX())));

        addPowerShotArrow(this);

        new TimedTask(3000) {
            @Override
            public void performAction() {
                destroy();
            }
        };
    }

    @Override
    public void hit() {

    }

    @Override
    public void destroyProjectile() {
        removePowerShotArrow(this);
    }

    @Override
    public void tickProjectile() {
        try{
            setX((float) (getX() + Math.cos(Math.toRadians(rectangle.getAngle())) * getSpeed() * GameView.density()));
            setY((float) (getY() - Math.sin(Math.toRadians(rectangle.getAngle())) * getSpeed() * GameView.density()));

            for(Unit x : Unit.getUnits()){
                if(x.getTeam() == archer.getTeam() ||
                        unitsHit.contains(x)) continue;
                if(Rectangle.intersect(rectangle, x.getCollisionBox())){
                    unitsHit.add(x);
                    x.damage(getDamage(), archer);
                }
            }
        }catch(Exception ignored){}

    }

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

    @Override
    public boolean setX(float x){
        super.setX(x);

        if(rectangle != null) rectangle.position((int) getX(), (int) getY());
        return true;
    }

    @Override
    public boolean setY(float y){
        super.setY(y);

        if(rectangle != null) rectangle.position((int) getX(), (int) getY());
        return true;
    }

    private static void addPowerShotArrow(final PowerShotArrow powerShotArrow){
        new Event() {
            @Override
            public void performAction() {
                    powerShotArrows.add(powerShotArrow);
            }
        };
    }

    private static void removePowerShotArrow(final PowerShotArrow powerShotArrow){
        new Event() {
            @Override
            public void performAction() {
                    powerShotArrows.remove(powerShotArrow);
            }
        };
    }
}
