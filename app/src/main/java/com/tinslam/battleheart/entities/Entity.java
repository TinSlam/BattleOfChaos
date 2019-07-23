package com.tinslam.battleheart.entities;

import android.graphics.Rect;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

/**
 * The class that includes all entities used in the game.
 */
public abstract class Entity{
    private float x, y;
    protected int image;
    private Rect collisionBox = new Rect();
    private static ArrayList<Entity> entities = new ArrayList<>();
    private static final Object entitiesLock = new Object();
    private Animation animation;
    private boolean exists = true;

    private boolean renderTeleport = false;
    private float teleportX, teleportY;
    private Animation teleportAnimation;

    /**
     * Constructor.
     * @param x The x position.
     * @param y The y position.
     */
    public Entity(float x, float y){
        this.x = x;
        this.y = y;

        addEntity(this);
    }

    /**
     * Destroys the entity. Removing it from the entities list and have it considered as a non existent entity.
     */
    public void destroy(){
        if(!doesExist()) return;

        destroyEntity();
        removeEntity(this);
        setExists(false);
    }

    /**
     * Destroys the entity.
     */
    public abstract void destroyEntity();

    /**
     * Ticks the entity.
     */
    public abstract void tickEntity();

    /**
     * Renders the entity.
     */
    public abstract void renderEntity(float xOffset, float yOffset);

    /**
     * Ticks the entity.
     */
    public void tick(){
        updateCollisionBox();
        tickEntity();
        updateCollisionBox();
    }

    /**
     * Updates the collision box of the entity. Setting it the same as the image. Must be overrode for more accurate hit boxes.
     */
    public abstract void updateCollisionBox();

    /**
     * Adds the entity to the entities list.
     */
    private static void addEntity(final Entity entity){
        new Event() {
            @Override
            public void performAction() {
                entities.add(entity);
            }
        };
    }

    /**
     * Removes the entity from the entities list.
     */
    private static void removeEntity(final Entity entity){
        new Event() {
            @Override
            public void performAction() {
                    entities.remove(entity);
            }
        };
    }

    /**
     * Renders the entity.
     */
    public void render(float xOffset, float yOffset){
        renderEntity(xOffset, yOffset);
        if(renderTeleport){
            teleportAnimation.render(xOffset + getCollisionBox().left, yOffset + getCollisionBox().top - getCollisionBox().height() / 3);
        }
    }

    /**
     *
     * @return The collision box of the entity.
     */
    public Rect getCollisionBox() {
        return collisionBox;
    }

    /**
     *
     * @return The x coordinate of the entity.
     */
    public float getX() {
        return x;
    }

    /**
     * Updates the x position of the entity if it's a possible position.
     */
    public boolean setX(float x) {
        this.x = x;
        return true;
    }

    /**
     *
     * @return The y coordinate of the entity.
     */
    public float getY() {
        return y;
    }

    /**
     * Updates the y position of the entity if it's a possible position.
     */
    public boolean setY(float y) {
        this.y = y;
        return true;
    }

    /**
     * Moves the entity along the x axis by the specified amount only if the move is possible.
     */
    protected boolean addX(float dx){
        return setX(getX() + dx);
    }

    /**
     * Moves the entity along the y axis by the specified amount only if the move is possible.
     */
    protected boolean addY(float dy){
        return setY(getY() + dy);
    }

    /**
     *
     * @return An ArrayList of all the existing entities.
     */
    public static ArrayList<Entity> getEntities() {
        return entities;
    }

    /**
     *
     * @return A lock that controls the synchronization of the actions done on the entities list.
     */
    public static Object getEntitiesLock() {
        return entitiesLock;
    }

    /**
     * Updates the animation of the unit. If the animation is the same as the current animation the animation will resume.
     * If the current animation is different than the new animation, ends the current one. Resets the animation and sets the image of the entity as the first image of the animation.
     * @param animation The new animation.
     */
    public void setAnimation(Animation animation) {
        if(this.animation == animation){
            if(animation == null) return;
            this.animation.resume();
            return;
        }
        if(this.animation != null){
            this.animation.hardStop();
        }
        this.animation = animation;
        if(this.animation == null) return;
        this.animation.reset();
        try{
            image = animation.getImages()[0];
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Stops the animation on the first image of the animation sent as the parameter. Making the entity having a static image.
     * @param animation The animation to get the image from.
     */
    public void setImage(final Animation animation){
        if(animation == null) return;
        setAnimation(animation);
        animation.pause();
        animation.resetIndex();
    }

    /**
     *
     * @return The animation of the entity.
     */
    public Animation getAnimation(){
        return animation;
    }

    /**
     *
     * @return Whether the entity exists or not.
     */
    public boolean doesExist(){
        return exists;
    }

    /**
     * Determining the existence of the entity.
     */
    private void setExists(boolean exists){
        this.exists = exists;
    }

    /**
     * @return The image if it has any.
     */
    public int getImage(){
        return image;
    }

    public void teleport(float x, float y){
        final Entity self = this;
        teleportX = x;
        teleportY = y;
        if(teleportAnimation == null){
            teleportAnimation = new Animation(Utils.resizeAnimation(AnimationLoader.teleport, getCollisionBox().width(), getCollisionBox().height() * 4 / 3),
                    150, 1, 0, 0) {

                @Override
                public void initAnimation() {

                }

                @Override
                public void halfWay() {
                    if(self instanceof Unit){
                        ((Unit) self).resetCommands();
                        ((Unit) self).setPosition(teleportX, teleportY);
                    }else{
                        self.setX(teleportX);
                        self.setY(teleportY);
                    }
                }

                @Override
                public void extraEffects() {

                }

                @Override
                public void finished() {
                    renderTeleport = false;
                }

                @Override
                public void onEnd() {

                }

                @Override
                public void onCycleEnd() {

                }
            };
        }
        renderTeleport = true;
        teleportAnimation.reset();
    }
}
