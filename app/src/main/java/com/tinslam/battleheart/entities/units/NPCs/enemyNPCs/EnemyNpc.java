package com.tinslam.battleheart.entities.units.NPCs.enemyNPCs;

import com.tinslam.battleheart.entities.units.NPCs.Npc;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.constants.Consts;

import java.util.ArrayList;

public abstract class EnemyNpc extends Npc {
    private static ArrayList<EnemyNpc> enemyNpcs = new ArrayList<>();
    private static final Object enemyNpcsLock = new Object();

    /**
     * Constructor.
     *
     * @param x The x position of the Npc.
     * @param y The y position of the Npc.
     */
    public EnemyNpc(float x, float y) {
        super(x, y, Consts.TEAM_ENEMY);

        addEnemyNpc(this);
    }

    protected abstract void tickEnemyNpc();

    protected abstract void renderEnemyNpc(float xOffset, float yOffset);

    protected abstract void destroyEnemyNpc();

    /**
     * Ticks the Npc.
     */
    @Override
    public void tickNpc() {
        tickEnemyNpc();
    }

    /**
     * Renders the Npc.
     */
    @Override
    public void renderNpc( float xOffset, float yOffset) {
        renderEnemyNpc(xOffset ,yOffset);
    }

    /**
     * Destroys the Npc.
     */
    @Override
    public void destroyNpc() {
        destroyEnemyNpc();
        removeEnemyNpc(this);
    }

    private static void addEnemyNpc(final EnemyNpc enemyNpc){
        new Event() {
            @Override
            public void performAction() {
                    enemyNpcs.add(enemyNpc);
            }
        };
    }

    private static void removeEnemyNpc(final EnemyNpc enemyNpc){
        new Event() {
            @Override
            public void performAction() {
                    enemyNpcs.remove(enemyNpc);
            }
        };
    }
}
