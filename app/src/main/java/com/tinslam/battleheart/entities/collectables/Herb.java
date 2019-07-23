package com.tinslam.battleheart.entities.collectables;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.quests.CollectHerbs;

import java.util.ArrayList;

public class Herb extends Collectable{
    private static final Object herbsLock = new Object();
    private static ArrayList<Herb> herbs = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param x The x position.
     * @param y The y position.
     */
    public Herb(float x, float y) {
        super(x, y);

        updateCollisionBox();

        addHerb(this);
    }

    @Override
    public void updateCollisionBox(){
        if(getCollisionBox() != null) getCollisionBox().set((int) getX(), (int) getY(), (int) (getX() + 32 * GameView.density()), (int) (getY() + 32 * GameView.density()));
    }

    @Override
    public void tickCollectable() {

    }

    @Override
    public void renderCollectable(float xOffset, float yOffset) {
        if(canBeCollected())
            MyGL2dRenderer.drawLabel(getX() + xOffset, getY() + yOffset, getCollisionBox().width(), getCollisionBox().height(),
                TextureData.color_blue, 255);
    }

    @Override
    public void destroyCollectable() {
        removeHerb(this);
    }

    public static void addHerb(Herb herb){
        herbs.add(herb);
    }

    public static void removeHerb(final Herb herb){
        new Event() {
            @Override
            public void performAction() {
                herbs.remove(herb);
            }
        };
    }

    public static Object getHerbsLock() {
        return herbsLock;
    }

    public static ArrayList<Herb> getHerbs() {
        return herbs;
    }
}
