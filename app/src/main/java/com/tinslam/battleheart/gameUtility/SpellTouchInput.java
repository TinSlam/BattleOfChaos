package com.tinslam.battleheart.gameUtility;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.interfaces.ActionInterface;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

/**
 * A class that handles touch inputs when a spell is to be casted.
 */
public abstract class SpellTouchInput implements ActionInterface {
    private static final Object touchInputsLock = new Object();
    private static ArrayList<SpellTouchInput> touchInputs = new ArrayList<>();

    private byte number = 1;
    private float[] points;
    private Entity entity;
    private Rect cancelArea;

    /**
     * The constructor.
     * @param entity The caster.
     * @param number The number of inputs needed.
     */
    protected SpellTouchInput(Entity entity, byte number, Rect cancelArea){
        this.number = number;
        this.entity = entity;
        this.cancelArea = cancelArea;
        points = new float[2 * number];

        addTouchInput(this);
    }

    /**
     * Is called when the inputs are given.
     */
    public abstract void performAction();

    /**
     * Handles motion events.
     */
    public void handleInput(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_UP :
                if(cancelArea != null){
                    if(Utils.isInRect(event.getX(), event.getY(), cancelArea)){
                        destroy();
                        return;
                    }
                }
                if(getNumber() != 0){
                    setNumber((byte) (getNumber() - 1));
                    points[getNumber() * 2] = event.getX() - BattleState.getBattleState().getCameraX();
                    points[getNumber() * 2 + 1] = event.getY() - BattleState.getBattleState().getCameraY();
                }
                if(getNumber() == 0){
                    destroy();
                    performAction();
                }
                break;
        }
    }

    /**
     * Clears all the touch inputs.
     */
    public static void clear(){
        for(SpellTouchInput x : touchInputs){
            x.destroy();
        }
    }

    /**
     * Destroys the touch input.
     */
    public void destroy(){
        if(GameView.getState().getTouchInput() == this){
            GameView.getState().setTouchInput(null);
        }
        removeTouchInput(this);
    }

    /**
     * Adds the touch input to touch inputs list.
     */
    private static void addTouchInput(final SpellTouchInput touchInput){
        new Event() {
            @Override
            public void performAction() {
                    touchInputs.add(touchInput);
            }
        };
    }

    /**
     * Removes the touch input from the touch inputs list.
     */
    private static void removeTouchInput(final SpellTouchInput touchInput){
        new Event() {
            @Override
            public void performAction() {
                touchInputs.remove(touchInput);
            }
        };
    }

    /**
     * Sets the number of touch inputs left.
     */
    public void setNumber(byte number){
        this.number = number;
    }

    /**
     * @return The number of touch inputs left.
     */
    public byte getNumber(){
        return number;
    }

    /**
     * @return The given inputs positions.
     */
    protected float[] getPoints(){
        return points;
    }

    /**
     * @return The caster.
     */
    public Entity getEntity(){
        return entity;
    }
}
