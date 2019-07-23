package com.tinslam.battleheart.UI.graphics.visualEffects;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.gameUtility.Event;

import java.util.ArrayList;

/**
 * The class that handles visual effects.
 */
public abstract class VisualEffect{
    public static final Object visualEffectsLock = new Object();
    private static ArrayList<VisualEffect> visualEffects = new ArrayList<>();

    protected float x, y;
    protected Animation animation;
    protected boolean ended = false;

    /**
     * Constructor.
     * @param animation The animation of the effect.
     * @param x The x position to draw the animation at.
     * @param y The y position to draw the animation at.
     */
    protected VisualEffect(Animation animation, float x, float y){
        this.animation = animation;
        this.x = x;
        this.y = y;

        addVisualEffect(this);

        if(animation != null) animation.reset();
    }

    /**
     * Ticks the visual effect.
     */
    public abstract void tickVisualEffect();

    /**
     * Renders the animation of the visual effect.
     */
    public void renderVisualEffect(float xOffset, float yOffset){
        if(!ended) animation.render(x + xOffset, y + yOffset);
    }

    /**
     * Ticks all visual effect.
     */
    public static void tick(){
        for(VisualEffect x : visualEffects){
            x.tickVisualEffect();
        }
    }

    /**
     * Renders all visual effects.
     */
    public static void render(float xOffset, float yOffset){
        for(VisualEffect x : visualEffects){
            x.renderVisualEffect(xOffset, yOffset);
        }
    }

    /**
     * Adds the visual effect.
     */
    private static void addVisualEffect(final VisualEffect ve){
        new Event() {
            @Override
            public void performAction() {
                    visualEffects.add(ve);
            }
        };
    }

    /**
     * Removes the visual effect.
     */
    protected static void removeVisualEffect(final VisualEffect ve){
        new Event() {
            @Override
            public void performAction() {
                ve.ended = true;
                ve.getAnimation().hardStop();
                visualEffects.remove(ve);
            }
        };
    }

    /**
     * Removes the visual effect that has the animation passed in.
     */
    protected static void removeVisualEffect(Animation animation){
        for(VisualEffect x : visualEffects){
            if(x.animation == animation){
                x.ended = true;
                animation.hardStop();
                removeVisualEffect(x);
                return;
            }
        }
    }

    /**
     * Removes all the visual effects. Is called at the end of a state.
     */
    public static void clear(){
        visualEffects.clear();
    }

    /**
     *
     * @return An ArrayList of all the existing visual effects.
     */
    public static ArrayList<VisualEffect> getVisualEffects() {
        return visualEffects;
    }

    /**
     * Sets the x position to have the animation drawn at.
     */
    protected void setX(float x){
        this.x = x;
    }

    /**
     * Sets the y position to have the animation drawn at.
     */
    protected void setY(float y){
        this.y = y;
    }

    /**
     *
     * @return The animation of the visual effect.
     */
    public Animation getAnimation(){
        return animation;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
