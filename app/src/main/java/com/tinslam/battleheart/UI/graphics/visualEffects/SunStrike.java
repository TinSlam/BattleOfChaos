package com.tinslam.battleheart.UI.graphics.visualEffects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.SpellConsts;

/**
 * A class that handles SunStrikes.
 */
public class SunStrike extends VisualEffect{
    private byte team = Consts.TEAM_ENEMY;
    private float damage = SpellConsts.SUN_STRIKE_DAMAGE;
    private int counter = 0;
    private int explosionDelay;
    private static int width = (int) (96 * GameView.density()), height = (int) (96 * GameView.density());
    private static int[] images = Utils.resizeAnimation(AnimationLoader.sunStrike, width, height);

    /**
     * Constructor.
     *
     * @param x The x position.
     * @param y The y position.
     */
    public SunStrike(int explosionDelay, float x, float y) {
        super(null, x, y);

        this.explosionDelay = explosionDelay;
    }

    /**
     * Explodes.
     */
    private void explode(){
        animation = new Animation(images, 120, 1, 0, 0) {
            @Override
            public void initAnimation() {

            }

            @Override
            public void halfWay() {
                for(Unit unit : Unit.getUnits()){
                    if(unit.getTeam() != team){
                        if(Utils.rectCollidesCircle(unit.getCollisionBox(), x + animation.getWidth() / 2, y + animation.getHeight() / 2, animation.getWidth() / 2)){
                            unit.damage(damage, unit);
                        }
                    }
                }
            }

            @Override
            public void extraEffects() {

            }

            @Override
            public void finished() {

            }

            @Override
            public void onEnd() {
                removeVisualEffect(this);
            }

            @Override
            public void onCycleEnd() {

            }
        };
        animation.reset();
    }

    /**
     * Ticks the visual effect.
     */
    @Override
    public void tickVisualEffect() {
        counter++;
        if(counter == explosionDelay) explode();
    }

    @Override
    public void renderVisualEffect(float xOffset, float yOffset){
        if(ended) return;
        if(getAnimation() != null){
            getAnimation().render(x + xOffset, y + yOffset);
        }else{
            MyGL2dRenderer.drawLabel((int) (x + xOffset), (int) (y + yOffset), width, height, TextureData.sun_strike_visual_indicator, 255);
        }
    }
}
