package com.tinslam.battleheart.UI.graphics.visualEffects.attachedToUnitVisualEffects;

import android.graphics.Bitmap;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.utils.Utils;

/**
 * The class that handles the healing visual effects.
 */
public class VisualEffectHeal extends VisualEffectAttachedToUnit{
    /**
     * Constructor.
     * @param unit The unit that the visual effect should apply to.
     */
    public VisualEffectHeal(Unit unit){
        super(unit, calcAnimation(unit), calcX(unit), calcY(unit));
    }

    /**
     * The method that resizes the animation to fit the unit.
     * @return A new instance of the resized animation.
     */
    private static Animation calcAnimation(Unit unit){
        int[] images = Utils.resizeAnimation(AnimationLoader.visualEffectHeal, unit.getCollisionBox().width());
        return new Animation(images, 100, 1, 0, 0) {
            @Override
            public void initAnimation() {

            }

            @Override
            public void halfWay() {

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
    }

    /**
     *
     * @return The x position to draw the effect at relative to the unit.
     */
    private static float calcX(Unit unit){
        return unit.getCollisionBox().left;
    }

    /**
     *
     * @return The y position to draw the effect at relative to the unit.
     */
    private static float calcY(Unit unit){
        return unit.getCollisionBox().bottom - unit.getCollisionBox().width() * 4 / 3;
    }

    /**
     * Updates the x position.
     */
    @Override
    public float updateX(Unit unit) {
        return calcX(unit);
    }

    /**
     * Updates the y position.
     */
    @Override
    public float updateY(Unit unit) {
        return calcY(unit);
    }

    /**
     * Is called on every frame.
     */
    @Override
    public void tickVisualEffectAttachedToUnit() {

    }
}