package com.tinslam.battleheart.entities.units.NPCs;

import android.graphics.Canvas;

import com.tinslam.battleheart.UI.graphics.visualEffects.BountyVisualEffect;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.Level;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

/**
 * The class that contains all the non playable characters.
 */
public abstract class Npc extends Unit{
    private static final Object npcsLock = new Object();
    private static ArrayList<Npc> npcs = new ArrayList<>();

    private int expOnDeath = 10;
    private int goldOnDeath = 5;
    private int goldOnDeathVariation = 2;

    /**
     * Constructor.
     * @param x The x position of the Npc.
     * @param y The y position of the Npc.
     */
    protected Npc(float x, float y, byte team) {
        super(x, y, team);

        addNpc(this);
    }

    /**
     * The AI behaviour of the Npc. Is called on every frame.
     */
    public abstract void ai();

    /**
     * Ticks the Npc.
     */
    public abstract void tickNpc();

    /**
     * Renders the Npc.
     */
    public abstract void renderNpc(float xOffset, float yOffset);

    /**
     * Destroys the Npc.
     */
    public abstract void destroyNpc();

    /**
     * Destroys the Npc. Removes it from the Npcs list.
     */
    @Override
    public void destroyUnit() {
        destroyNpc();
        removeNpc(this);
    }

    /**
     * Ticks the Npc. Runs the AI.
     */
    @Override
    public void tickUnit() {
        for(Pc pc : Pc.getPcs()){
            if(Utils.distance(this, pc.getCollisionBox().centerX(), pc.getCollisionBox().centerY()) < GameView.getScreenWidth()){
                ai();
                break;
            }
        }
        tickNpc();
    }

    /**
     * Renders the Npc.
     */
    @Override
    public void renderUnit(float xOffset, float yOffset) {
        try{
            renderNpc(xOffset, yOffset);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Adds the Npc to the Npcs list.
     */
    private void addNpc(final Npc npc){
        new Event() {
            @Override
            public void performAction() {
                    npcs.add(npc);
            }
        };
    }

    /**
     * Removes the Npc from the Npcs list. Spawns new wave if there are no npcs left.
     */
    private void removeNpc(final Npc npc){
        new Event() {
            @Override
            public void performAction() {
                if(npcs.remove(npc)){
                    if(!BattleState.getBattleState().isOver()){
                        BattleState.getBattleState().addExpGained(expOnDeath);
                        int difference = Utils.getRandomIntegerInTheRange(-goldOnDeathVariation, goldOnDeathVariation, null);
                        BattleState.getBattleState().addGoldGained(goldOnDeath + difference);
                        new BountyVisualEffect(getCollisionBox().centerX(), getCollisionBox().centerY(), goldOnDeath + difference);
                        if(Npc.getNpcs().isEmpty()){
                            if(Level.getLevel() != null){
                                Level.getLevel().spawnWave();
                            }
                        }
                    }
                }
            }
        };
    }

    /**
     *
     * @return A lock that controls the synchronization of the actions preformed on the Npcs list.
     */
    public static Object getNpcsLock() {
        return npcsLock;
    }

    /**
     *
     * @return An ArrayList of all the existing Npcs.
     */
    public static ArrayList<Npc> getNpcs(){
        return npcs;
    }
}