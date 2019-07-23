package com.tinslam.battleheart.entities.projectiles;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses.GiantTroll;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.utils.Utils;

public class GiantTrollAxe extends Projectile{
    private GiantTroll giantTroll;
    private int rotationAngle = 0;
    private float angle = 90;
    private int radius = (int) (32 * GameView.density());
    private float centerX, centerY;
    private byte phase = 1;
    private float rx, ry;
    private boolean reverse = false;
    private boolean yReverse = false;

    /**
     * Constructor.
     *
     * @param x  The x position.
     * @param y  The y position.
     */
    public GiantTrollAxe(float x, float y, float ovalWidth, float ovalHeight, float x2, float y2, GiantTroll giantTroll) {
        super(x, y, x2, y2);

        this.giantTroll = giantTroll;

        centerX = x;
        centerY = y - ovalHeight;
        rx = ovalWidth;
        ry = ovalHeight;

        if(x < x2){
            reverse = true;
        }

        if(y2 < y){
            yReverse = true;
            centerY = y + ovalHeight;
        }

        updateCollisionBox();
    }

    @Override
    public void updateCollisionBox(){
        if(getCollisionBox() != null) getCollisionBox().set((int) getX(), (int) getY(), (int) (getX() + 2 * radius), (int) (getY() + 2 * radius));
    }

    /**
     * Is called when target is hit.
     */
    @Override
    public void hit() {
        for(Pc pc : Pc.getPcs()){
            if(Utils.rectCollidesCircle(pc.getCollisionBox(), getX2(), getY2(), radius)){
                pc.damage(giantTroll.getAxeDamage(), giantTroll);
            }
        }
    }

    /**
     * Is called when destroyed.
     */
    @Override
    public void destroyProjectile() {

    }

    /**
     * Is called on every frame.
     */
    @Override
    public void tickProjectile() {
        if(angle == 0){
            phase = 2;
            rx = Math.abs(getX2() - getX());
            ry = Math.abs(centerY - getY2());
            centerX = getX2();
        }else if(angle == 90 && phase == 2){
            hit();
            destroy();
        }
        angle += 5;
        if(angle == 360) angle = 0;
        if(!reverse){
            setX((float) (centerX + rx * Math.cos(Math.toRadians(angle))));
        }else{
            setX((float) (centerX - rx * Math.cos(Math.toRadians(angle))));
        }
        if(!yReverse){
            setY((float) (centerY + ry * Math.sin(Math.toRadians(angle))));
        }else{
            setY((float) (centerY - ry * Math.sin(Math.toRadians(angle))));
        }
    }

    /**
     * Draws the projectile.
     */
    @Override
    public void renderProjectile(float xOffset, float yOffset) {
        MyGL2dRenderer.drawLabel((int) (getX() + xOffset - radius),
                (int) (getY() + yOffset - radius),
                2 * radius,
                2 * radius,
                TextureData.giantAxe,
                255,
                rotationAngle);
        rotationAngle += 36;
        MyGL2dRenderer.drawLabel(getX2() + xOffset - radius,
                getY2() + yOffset - radius,
                2 * radius,
                2 * radius,
                TextureData.sun_strike_visual_indicator ,
                255);
    }
}
