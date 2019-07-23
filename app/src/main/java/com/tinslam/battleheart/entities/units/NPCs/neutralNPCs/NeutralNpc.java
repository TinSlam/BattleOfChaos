package com.tinslam.battleheart.entities.units.NPCs.neutralNPCs;

import android.graphics.Canvas;

import com.tinslam.battleheart.entities.units.NPCs.Npc;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.constants.Consts;

import java.util.ArrayList;

public abstract class NeutralNpc extends Npc {
    private static ArrayList<NeutralNpc> neutralNpcs = new ArrayList<>();
    private static final Object neutralNpcsLock = new Object();

    /**
     * Constructor.
     *
     * @param x The x position of the Npc.
     * @param y The y position of the Npc.
     */
    NeutralNpc(float x, float y) {
        super(x, y, Consts.TEAM_NEUTRAL);

        setVulnerability(false);

        addNeutralNpc(this);
    }

    protected abstract void tickNeutralNpc();

    protected abstract void renderNeutralNpc(float xOffset, float yOffset);

    protected abstract void destroyNeutralNpc();

    /**
     * Ticks the Npc.
     */
    @Override
    public void tickNpc() {
        tickNeutralNpc();
    }

    /**
     * Renders the Npc.
     */
    @Override
    public void renderNpc(float xOffset, float yOffset) {
        renderNeutralNpc(xOffset, yOffset);
    }

    /**
     * Destroys the Npc.
     */
    @Override
    public void destroyNpc() {
        destroyNeutralNpc();
        removeNeutralNpc(this);
    }

    private static void addNeutralNpc(final NeutralNpc neutralNpc){
        new Event() {
            @Override
            public void performAction() {
                    neutralNpcs.add(neutralNpc);
            }
        };
    }

    private static void removeNeutralNpc(final NeutralNpc neutralNpc){
        new Event() {
            @Override
            public void performAction() {
                    neutralNpcs.remove(neutralNpc);
            }
        };
    }
}
