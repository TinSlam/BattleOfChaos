package com.tinslam.battleheart.spells;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.states.ArenaState;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.SpellConsts;

import java.util.ArrayList;

/**
 * A class that holds all the spells.
 */
public abstract class Spell{
    private static final Object spellsLock = new Object();
    private static ArrayList<Spell> spells = new ArrayList<>();

    private boolean ready = true;
    private int cooldown = 5000;
    private int cdLeft = 0;
    Unit caster;
    private Texture portrait;
    private String name = "";
    int spellsCastedWhileOnCooldown = 0;
    private boolean targeting = false;
    protected Rect cancelArea = null;

    /**
     * Constructor.
     */
    Spell(Unit caster, Texture portrait) {
        this.caster = caster;
        this.portrait = portrait;

        addSpell(this);
    }

    /**
     * Casts the spell.
     */
    protected abstract void castSpell();

    /**
     * Is called when the spell button is clicked.
     */
    public abstract void onClick();

    /**
     * Is called when the spell button is clicked.
     */
    public void cast(){
        if(ready){
            if(caster == null) return;
            if(!caster.doesExist()) return;
            spellsCastedWhileOnCooldown = 0;
            for(Spell x : spells){
                x.spellsCastedWhileOnCooldown++;
            }
            ready = false;
            cdLeft = cooldown;
            new TimedTaskRepeat(20) {
                @Override
                public boolean checkCondition() {
                    return !(cdLeft > 0);
                }

                @Override
                public void performAction() {
                    if(GameView.isActive()) cdLeft -= 20;
                }

                @Override
                public void end() {
                    cdLeft = 0;
                    ready = true;
                }
            };
            castSpell();
            BattleState.getBattleState().setSpellCasterPortrait(PlayerStats.getUnitPortrait(getCaster().getClass().getSimpleName()));
            GameView.getState().setSlowMo(true);
            new TimedTask(SpellConsts.SLOW_MO_TIME) {
                @Override
                public void performAction() {
                    GameView.getState().setSlowMo(false);
                }
            };
        }
    }

    /**
     * @return The percentage value left of the cooldown.
     */
    public float getCdLeftPercentage(){
        return (float) cdLeft / cooldown * 100;
    }

    /**
     * Adds the spell to the spells list.
     */
    private static void addSpell(Spell spell){
        spells.add(spell);
    }

    /**
     * Clears the list of all spells.
     */
    public static void clear(){
        spells.clear();
    }

    /**
     * @return Whether the spell is ready or on CD.
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * @return The spell portrait.
     */
    public Texture getPortrait() {
        return portrait;
    }

    /**
     * @return The name of the spell.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the spell.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The caster.
     */
    public Unit getCaster() {
        return caster;
    }

    /**
     * @return Whether the spell is point targeting or not.
     */
    public boolean isTargeting() {
        return targeting;
    }

    /**
     * Sets the point targeting value of the spell.
     */
    void setTargeting(@SuppressWarnings("SameParameterValue") boolean targeting) {
        this.targeting = targeting;
    }

    public void setCancelArea(Rect cancelArea) {
        this.cancelArea = cancelArea;
    }

    public Rect getCancelArea() {
        return cancelArea;
    }
}
