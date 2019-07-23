package com.tinslam.battleheart.entities.projectiles;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses.GiantTroll;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.utils.Utils;

public class GiantTrollFallingRock extends Projectile{
    private GiantTroll giantTroll;
    private float speed = 20 * GameView.density();
    private static int[] textures = {TextureData.giantRock1, TextureData.giantRock2, TextureData.giantRock3};
    private int texture;

    /**
     * Constructor.
     */
    public GiantTrollFallingRock(float x1, float y1, float x2, float y2, GiantTroll giantTroll) {
        super(x1, y1, x2, y2);

        this.giantTroll = giantTroll;

        texture = Utils.getRandomIntegerInTheRange(0, textures.length - 1, null);

        updateCollisionBox();
    }

    /**
     * Is called when target is hit.
     */
    @Override
    public void hit() {
        for(Pc pc : Pc.getPcs()){
            if(Utils.rectCollidesCircle(pc.getCollisionBox(), getX2(), getY2(), 32 * GameView.density())){
                pc.damage(giantTroll.getRockDamage(), giantTroll);
            }
        }
    }

    @Override
    public void updateCollisionBox(){
        if(getCollisionBox() != null) getCollisionBox().set((int) getX(), (int) getY(), (int) (getX() + 64 * GameView.density()), (int) (getY() + 64 * GameView.density()));
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
        setY(getY() + speed);
        if(Math.abs(getY2() - getY()) < speed){
            hit();
            destroy();
        }
    }

    /**
     * Draws the projectile.
     *
     */
    @Override
    public void renderProjectile(float xOffset, float yOffset) {
        MyGL2dRenderer.drawLabel(getX2() + xOffset, getY2() + yOffset, 64 * GameView.density(), 64 * GameView.density(),
                TextureData.sun_strike_visual_indicator, 255);
        MyGL2dRenderer.drawLabel(getX() + xOffset, getY() + yOffset, 64 * GameView.density(), 64 * GameView.density(),
                textures[texture], 255);
    }
}
