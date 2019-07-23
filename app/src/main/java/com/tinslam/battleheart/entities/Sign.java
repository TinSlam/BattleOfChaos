package com.tinslam.battleheart.entities;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

public class Sign extends Entity{
    private static final Object signsLock = new Object();
    private static ArrayList<Sign> signs = new ArrayList<>();
    private static Texture staticImage = new Texture(TextureData.sign, (int) (32 * GameView.density()), (int) (32 * GameView.density()));

    private String text = "";

    /**
     * Constructor.
     *
     * @param x The x position.
     * @param y The y position.
     */
    public Sign(float x, float y) {
        super(x, y);

        image = staticImage.getTexture();
        updateCollisionBox();

        addSign(this);
    }

    @Override
    public void updateCollisionBox(){
        if(getCollisionBox() != null) getCollisionBox().set((int) getX(), (int) getY(), (int) (getX() + staticImage.getWidth()), (int) (getY() + staticImage.getHeight()));
    }

    public void setText(String text){
        this.text = text;
    }

    /**
     * Destroys the entity.
     */
    @Override
    public void destroyEntity() {
        removeSign(this);
    }

    /**
     * Ticks the entity.
     */
    @Override
    public void tickEntity() {

    }

    /**
     * Renders the entity.
     */
    @Override
    public void renderEntity(float xOffset, float yOffset) {
        MyGL2dRenderer.drawLabel(getX() + xOffset, getY() + yOffset, getCollisionBox().width(), getCollisionBox().height(), image, 255);
    }

    /**
     * Is called when clicked.
     */
    private boolean onActionUp(int mx, int my){
        if(Utils.isInRect(mx, my, getCollisionBox())) {
            ((DungeonState) BattleState.getBattleState()).showMessage(text);
            return true;
        }
        return false;
    }

    /**
     * Calls onUp() on every existing sign.
     */
    public static boolean onUp(int x, int y){
        for(Sign sign : signs){
            if(sign.onActionUp(x, y)) return true;
        }

        return false;
    }

    /**
     * Adds sign to signs list.
     */
    private static void addSign(final Sign sign){
        new Event() {
            @Override
            public void performAction() {
                signs.add(sign);
            }
        };
    }

    /**
     * Removes sign from the list.
     */
    private static void removeSign(final Sign sign){
        new Event() {
            @Override
            public void performAction() {
                signs.remove(sign);
            }
        };
    }
}
