package com.tinslam.battleheart.UI.graphics.visualEffects;

import android.graphics.Bitmap;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.utils.Utils;

public class MoveCommandVisualEffect extends VisualEffect{
    private static int[] images = Utils.resizeAnimation(AnimationLoader.sunStrike, 32 * GameView.density(), 32 * GameView.density());
    /**
     * Constructor.
     *
     * @param x         The x position to draw the animation at.
     * @param y         The y position to draw the animation at.
     */
    public MoveCommandVisualEffect(float x, float y) {
        super(calcAnimation(), x - images[images.length - 2] / 2, y - images[images.length - 1] / 2);
    }

    private static Animation calcAnimation(){
        return new Animation(images, 100, 1, 0, 0){
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
                removeVisualEffect(this);
            }

            @Override
            public void onEnd() {

            }

            @Override
            public void onCycleEnd() {

            }
        };
    }

    /**
     * Ticks the visual effect.
     */
    @Override
    public void tickVisualEffect() {

    }
}
