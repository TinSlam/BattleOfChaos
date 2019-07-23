package com.tinslam.battleheart.entities.collectables;

import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.quests.CollectQuest;

import java.util.ArrayList;

public abstract class Collectable extends Entity{
    private static final Object collectablesLock = new Object();
    private static ArrayList<Collectable> collectables = new ArrayList<>();

    private boolean canBeCollected = false;

    /**
     * Constructor.
     *
     * @param x The x position.
     * @param y The y position.
     */
    Collectable(float x, float y) {
        super(x, y);

        addCollectable(this);
    }

    public void interact(){
        if(doesExist() && canBeCollected()){
            destroy();
            CollectQuest.collectableCollected(getClass());
        }
    }

    public abstract void tickCollectable();

    public abstract void renderCollectable(float xOffset, float yOffset);

    public abstract void destroyCollectable();

    /**
     * Destroys the entity.
     */
    @Override
    public void destroyEntity() {
        destroyCollectable();
        removeCollectable(this);
    }

    /**
     * Ticks the entity.
     */
    @Override
    public void tickEntity() {
        tickCollectable();
    }

    /**
     * Renders the entity.
     */
    @Override
    public void renderEntity(float xOffset, float yOffset) {
        renderCollectable(xOffset, yOffset);
    }

    private static void addCollectable(final Collectable collectable){
        new Event() {
            @Override
            public void performAction() {
                collectables.add(collectable);
            }
        };
    }

    private static void removeCollectable(final Collectable collectable){
        new Event() {
            @Override
            public void performAction() {
                collectables.remove(collectable);
            }
        };
    }

    public boolean canBeCollected(){
        return canBeCollected;
    }

    public void setCanBeCollected(boolean flag){
        canBeCollected = flag;
    }

    public static Object getCollectablesLock() {
        return collectablesLock;
    }

    public static ArrayList<Collectable> getCollectables() {
        return collectables;
    }
}