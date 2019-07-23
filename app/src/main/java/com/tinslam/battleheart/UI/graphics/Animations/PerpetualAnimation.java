package com.tinslam.battleheart.UI.graphics.Animations;

import com.tinslam.battleheart.entities.Entity;

/**
 * The class that handles the moving animations.
 */
public class PerpetualAnimation extends Animation{
    private Entity entity;

    /**
     * Instantiates an instance of attack animation.
     * @param images An array of images that create the animation.
     * @param interval The time in milliseconds that each image is shown.
     * @param entity The entity associated to the animation.
     */
    public PerpetualAnimation(int[] images, long interval, Entity entity) {
        super(images, interval);

        this.entity = entity;
    }

    /**
     * Instantiates an instance of attack animation.
     * @param images An array of images that create the animation.
     * @param interval The time in milliseconds that each image is shown.
     * @param repeat How many times the animation repeat. Pass in -1 for infinite loop.
     * @param xDrawn The xOffset of each image compared to the x coordinate of the entity.
     * @param yDrawn The yOffset of each image compared to the y coordinate of the entity.
     * @param entity The entity associated to the animation.
     */
    public PerpetualAnimation(int[] images, long interval, int repeat, float xDrawn, float yDrawn, Entity entity) {
        super(images, interval, repeat, xDrawn, yDrawn);

        this.entity = entity;
    }

    @Override
    public void tickAnimation(){
        if(!entity.doesExist()){
            hardStop();
        }
        super.tickAnimation();
    }
    /**
     * Initializes the animation.
     */
    @Override
    public void initAnimation() {

    }

    /**
     * Is called when the animation is halfway done.
     * This is where the attack is done.
     */
    @Override
    public void halfWay() {

    }

    /**
     * Is called when initializing the animation.
     */
    @Override
    public void extraEffects() {

    }

    /**
     * Is called when the animation is finished.
     */
    @Override
    public void finished() {

    }

    /**
     * Is called when the animation is completely over.
     */
    @Override
    public void onEnd() {

    }

    /**
     * Is called when a full cycle of the animation ends.
     */
    @Override
    public void onCycleEnd() {

    }
}
