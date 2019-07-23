package com.tinslam.battleheart.UI.graphics.visualEffects;

import android.graphics.Color;
import android.graphics.Paint;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.utils.Utils;

/**
 * A class for the bounty visual effect.
 */
public class BountyVisualEffect extends VisualEffect{
    private TextRenderer textRenderer;

    /**
     * Constructor.
     *
     * @param x         The x position to draw the animation at.
     * @param y         The y position to draw the animation at.
     */
    public BountyVisualEffect(float x, float y, int bounty) {
        super(calcAnimation(), x, y);

        textRenderer = new TextRenderer("" + bounty,
                x + animation.getWidth() + 2 * GameView.density(),
                y + animation.getHeight() / 2,
                animation.getHeight() * 2,
                animation.getHeight(),
                Paint.Align.LEFT,
                false, true);

//        textRenderer.setTextColor(Color.YELLOW);
        textRenderer.setTextAlpha(255);
        textRenderer.show();
    }

    /**
     * Instantiates the used animation.
     */
    private static Animation calcAnimation(){
        return new Animation(AnimationLoader.bountyVisualEffect, 1000) {
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

            }

            @Override
            public void onCycleEnd() {

            }
        };
    }

    /**
     * Is called on every frame.
     */
    @Override
    public void tickVisualEffect() {
        y--;
        if(textRenderer != null){
            textRenderer.translate(0, -1);
            textRenderer.setTextAlpha(Utils.max(0, textRenderer.getTextAlpha() - 10));
            if(textRenderer.getTextAlpha() == 0){
                textRenderer.destroy();
                removeVisualEffect(this);
            }
        }
    }

    /**
     * Renders the animation of the visual effect.
     */
    @Override
    public void renderVisualEffect(float xOffset, float yOffset){
        if(ended) return;
        if(textRenderer != null){
            animation.render(x + xOffset, y + yOffset, textRenderer.getTextAlpha());
//            textRenderer.drawText(canvas, xOffset, yOffset);
        }
    }
}
