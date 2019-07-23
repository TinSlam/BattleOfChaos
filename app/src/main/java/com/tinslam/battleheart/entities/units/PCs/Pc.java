package com.tinslam.battleheart.entities.units.PCs;

import android.graphics.Canvas;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.constants.Consts;

import java.util.ArrayList;

/**
 * The class that contains all the playable characters.
 */
public abstract class Pc extends Unit{
    private static final Object pcsLock = new Object();
    private static ArrayList<Pc> pcs = new ArrayList<>();

    /**
     * Constructor.
     * @param x The x position of the Pc.
     * @param y The y position of the Pc.
     */
    Pc(float x, float y) {
        super(x, y, Consts.TEAM_ALLIED);

        addPc(this);
    }

    /**
     *
     * @return A lock that controls the synchronization of the actions done on the Pcs list.
     */
    public static Object getPcsLock() {
        return pcsLock;
    }

    /**
     *
     * @return An ArrayList of all the existing Pcs.
     */
    public static ArrayList<Pc> getPcs() {
        return pcs;
    }

    /**
     * A method that decides which unit must be prioritized to be chosen when a spot on the screen is touched that contains multiple units overlapped at it.
     * @param units An ArrayList of all the overlapped units.
     * @return The chosen unit.
     */
    public abstract Unit choosePriorityUnit(ArrayList<Unit> units);

    /**
     * Performs the action to be done by a touch command.
     */
    public abstract void performAction();

    /**
     * Ticks the Pc.
     */
    public abstract void tickPc();

    /**
     * Renders the Pc.
     */
    public abstract void renderPc(float xOffset, float yOffset);

    /**
     * Destroys the Pc.
     */
    public abstract void destroyPc();

    /**
     * Destroys the Pc. Removes it from the Pcs list.
     */
    @Override
    public void destroyUnit() {
        if(!BattleState.getBattleState().isOver()) BattleState.getBattleState().removeSpellsOfTheUnit(this);
        destroyPc();
        removePc(this);
    }

    /**
     * Ticks the Pc.
     */
    @Override
    public void tickUnit() {
        tickPc();
    }

    /**
     * Renders the Pc.
     */
    @Override
    public void renderUnit(float xOffset, float yOffset) {
        renderPc(xOffset, yOffset);
    }

    /**
     * Adds the Pc to the Pcs list.
     */
    private void addPc(final Pc pc){
        new Event() {
            @Override
            public void performAction() {
                pcs.add(pc);
            }
        };
    }

    /**
     * Removes the Pc from the Pcs list. Checks for lose requirements.
     */
    private void removePc(final Pc pc){
        new Event() {
            @Override
            public void performAction() {
                if(BattleState.getBattleState().getSelectedPc() == pc) BattleState.getBattleState().setSelectedPc(null);
                if(pcs.remove(pc)){
                    BattleState.getBattleState().pcDied(pc);
                    if(!BattleState.getBattleState().isOver()) if(Pc.getPcs().isEmpty()) BattleState.getBattleState().lose();
                }
            }
        };
    }
}
