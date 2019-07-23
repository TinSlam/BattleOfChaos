package com.tinslam.battleheart.UI.graphics.Animations;

import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.entities.units.PCs.Knight;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.interfaces.AnimationInterface;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;

import java.sql.SQLOutput;
import java.util.ArrayList;

/**
 * The class that handles animations.
 */
public abstract class Animation implements AnimationInterface {
    private int index = 0;
    private long interval = 0;
    private int width = 0, height = 0;
    private int[] images;
    private int repeat = -1;
    private boolean stop = false;
    private boolean pause = false;
    private boolean softStop = false;
    private float x = 0, y = 0, xDrawn = 0, yDrawn = 0;
    private boolean doneFor = false;
    private float counter = 0;
    private int cycle = 0;

    private static ArrayList<Animation> animations = new ArrayList<>();
    private static final Object animationsLock = new Object();

    /**
     * Instantiates an instance of attack animation.
     * @param images An array of images that create the animation.
     * @param interval The time in milliseconds that each image is shown.
     */
    public Animation(final int[] images, final long interval) {
        this.interval = interval;
        this.width = images[images.length - 2];
        this.height = images[images.length - 1];
        this.images = new int[images.length - 2];
        System.arraycopy(images, 0, this.images, 0, images.length - 2);
    }

    /**
     * Constructor.
     * @param images an array of images that create the animation.
     * @param interval The time in milliseconds that each image is shown.
     * @param repeat The number of times the animation repeats. Passing -1 will result in an infinite loop.
     * @param xDrawn The xOffset of each image compared to the x coordinate of the entity.
     * @param yDrawn The yOffset of each image compared to the y coordinate of the entity.
     */
    public Animation(int[] images, long interval, int repeat, float xDrawn, float yDrawn){
        this.width = images[images.length - 2];
        this.height = images[images.length - 1];
        this.images = new int[images.length - 2];
        System.arraycopy(images, 0, this.images, 0, images.length - 2);
        this.interval = interval;
        this.repeat = repeat;
        this.xDrawn = xDrawn;
        this.yDrawn = yDrawn;
    }

    public static void tick(){
        try{
            for(Animation animation : animations){
                animation.tickAnimation();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Initializes the animation.
     */
    public abstract void initAnimation();

    /**
     * Is called when the animation is halfway done.
     */
    public abstract void halfWay();

    /**
     * Is called when initializing the animation.
     */
    public abstract void extraEffects();

    /**
     * Is called when animation is finished. Whether it was forced to stop or it ended. Is run after the onEnd() method.
     */
    public abstract void finished();

    void tickAnimation(){
        counter += Utils.frameInMilliSeconds();
        if(counter >= interval){
            counter = 0;
            if(stop){
                stopAnimation();
                return;
            }
            if(pause) return;
            if(index < images.length - 1){
                if(index == images.length / 2) halfWay();
                index++;
            }else{
                if(repeat == -1){
                    index = 0;
                    onCycleEnd();
                    if(softStop){
                        onEnd();
                    }
                }else{
                    cycle++;
                    index = 0;
                    onCycleEnd();
                    if(softStop){
                        onEnd();
                        stopAnimation();
                        return;
                    }
                    if(cycle == repeat){
                        onEnd();
                        stopAnimation();
                        return;
                    }
                }
            }
        }
    }

    private void stopAnimation(){
        finished();
        cleanUp();
        final Animation self = this;
        new Event() {
            @Override
            public void performAction() {
                animations.remove(self);
            }
        };
    }

    /**
     * Runs the animation.
     */
    private void startThread(){
        final Animation self = this;
        new Event() {
            @Override
            public void performAction() {
                animations.add(self);
                initAnimation();
            }
        };
    }

    /**
     * Ends all the animations.
     */
    public static void endAnimations(){
        for(Animation x : animations){
            x.hardStop();
            x.setDoneFor(true);
        }
        animations.clear();
    }

    /**
     * Pauses the animation.
     */
    public void pause(){
        if(!isDoneFor()) pause = true;
    }

    /**
     * Resumes the animation.
     */
    public void resume(){
        pause = false;
    }

    /**
     * Stops the animation. Does not run the methods that are normally called when the animation finishes uninterrupted.
     */
    public void hardStop(){
        stop = true;
//        if(threadDone) return;
        resume();
    }

    /**
     * Makes the current cycle be the last cycle of the animation. Runs the onEnd() and onCycleEnd() methods when done.
     */
    public void softStop(){
        if(pause) resume();
        softStop = true;
    }

    /**
     * Resets the animation.
     */
    public void reset(){
        if(pause) resume();
        hardStop();
        cleanUp();
        startThread();
    }

    /**
     * Resets some variables.
     */
    private void cleanUp(){
        softStop = false;
        cycle = 0;
        counter = 0;
        stop = false;
        pause = false;
        index = 0;
    }

    /**
     * Resets the index of the shown image to 0. Meaning first image in the array will be shown.
     */
    public void resetIndex(){
        index = 0;
    }

    /**
     * Renders the animation at the given positions.
     */
    public void render(float x, float y){
        MyGL2dRenderer.drawLabel((int) (x - xDrawn), (int) (y - yDrawn), width, height, images[index], 255);
    }

    /**
     * Renders the animation at the given positions using the given paint object.
     */
    public void render(float x, float y, int alpha){
        MyGL2dRenderer.drawLabel((int) (x - xDrawn), (int) (y - yDrawn), width, height, images[index], alpha);
    }

    /**
     * Renders the animation using the matrix passed in and translating it with the given offset.
     */
    public void render(float[] matrix, float xOffset, float yOffset){
        matrix[0] += xOffset;
        matrix[1] += yOffset;
        MyGL2dRenderer.drawLabel(images[index], 255, matrix);
    }

    /**
     * Is called when the animation is completely over.
     */
    public abstract void onEnd();

    /**
     * Is called when a full cycle of the animation ends.
     */
    public abstract void onCycleEnd();

    /**
     *
     * @return The width of the images in the animation.
     */
    public int getWidth(){
        return width;
    }

    /**
     *
     * @return The height of the images in the animation.
     */
    public int getHeight(){
        return height;
    }

    /**
     *
     * @return The x position the animation is drawn at.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x position the animation is drawn at.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     *
     * @return The y position the animation is drawn at.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y position the animation is drawn at.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @return An array of the images in the animation.
     */
    public int[] getImages() {
        return images;
    }

    /**
     *
     * @return Whether the animation thread must end or can still keep going.
     */
    private boolean isDoneFor(){
        return doneFor;
    }

    /**
     * Determines whether the animation thread must end or can still keep going.
     */
    private void setDoneFor(@SuppressWarnings("SameParameterValue") boolean flag){
        doneFor = flag;
    }
}