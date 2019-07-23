package com.tinslam.battleheart.UI.graphics.Animations;

import com.tinslam.battleheart.entities.units.PCs.Knight;
import com.tinslam.battleheart.entities.units.Unit;

/**
 * The class that handles the attack animations.
 */
public abstract class CastAnimation extends Animation{
    private Unit unit;
    private Animation nextAnimation;

    /**
     * Instantiates an instance of attack animation.
     * @param images An array of images that create the animation.
     * @param interval The time in milliseconds that each image is shown.
     * @param unit The unit this animation belongs to.
     * @param nextAnimation The animation to go to once this one ends.
     */
    public CastAnimation(int[] images, long interval, Unit unit, Animation nextAnimation) {
        super(images, interval);

        this.unit = unit;
        this.nextAnimation = nextAnimation;
    }

    /**
     * Instantiates an instance of attack animation.
     * @param images An array of images that create the animation.
     * @param interval The time in milliseconds that each image is shown.
     * @param repeat How many times the animation repeat. Pass in -1 for infinite loop.
     * @param xDrawn The xOffset of each image compared to the x coordinate of the entity.
     * @param yDrawn The yOffset of each image compared to the y coordinate of the entity.
     * @param unit The unit this animation belongs to.
     * @param nextAnimation The animation to go to once this one ends.
     */
    public CastAnimation(int[] images, long interval, int repeat, float xDrawn, float yDrawn, Unit unit, Animation nextAnimation) {
        super(images, interval, repeat, xDrawn, yDrawn);

        this.unit = unit;
        this.nextAnimation = nextAnimation;
    }

    @Override
    public void tickAnimation(){
        if(!unit.doesExist()){
            hardStop();
        }
        super.tickAnimation();
    }

    /**
     * Is called when the animation is halfway done.
     * This is where the attack is done.
     */
    @Override
    public void halfWay(){
        if(unit.getAnimation() != this) return;
        if(!unit.doesExist()) return;
        castHalfWay();
    }

    public abstract void castAtStart();
    public abstract void castHalfWay();
    public abstract void castOnEnd();

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
        castOnEnd();
        if(unit.getAnimation() == this){
            if(unit instanceof Knight){
                unit.setAnimation(nextAnimation);
            }else{
                unit.setImage(nextAnimation);
            }
        }
        unit.resetCommands();
    }

    /**
     * Initializes the animation.
     */
    @Override
    public void initAnimation(){
        extraEffects();
        castAtStart();
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
