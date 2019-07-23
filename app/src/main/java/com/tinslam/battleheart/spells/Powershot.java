package com.tinslam.battleheart.spells;

import android.graphics.Bitmap;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.entities3D.Dungeon;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.SpellTouchInput;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.projectiles.PowerShotArrow;
import com.tinslam.battleheart.entities.units.PCs.Archer;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.utils.constants.SpellConsts;

/**
 * A class for the Powershot spell.
 */
public class Powershot extends Spell{
    private static Texture staticImage = new Texture(TextureData.portrait_powershot, SpellConsts.PORTRAIT_WIDTH, SpellConsts.PORTRAIT_HEIGHT);

    /**
     * Constructor.
     */
    public Powershot(Unit caster) {
        super(caster, staticImage);

        setTargeting(true);
        setName(GameView.string(R.string.spell_powershot));
    }

    /**
     * Casts the spell.
     */
    @Override
    public void castSpell(){

    }

    /**
     * Is called when the spell button is clicked.
     */
    @Override
    public void onClick() {
        final Spell self = this;
        GameView.getState().setTouchInput(new SpellTouchInput(caster, (byte) 1, cancelArea) {
            @Override
            public void performAction() {
                new Event() {
                    @Override
                    public void performAction() {
                        if(caster == null) return;
                        if(!caster.doesExist()) return;
                        float[] points = getPoints();
                        Archer archer = (Archer) caster;
                        if(BattleState.getBattleState() instanceof DungeonState){
                            DungeonState.getDungeonState().replayShootPowershot(self, points[0], points[1]);
                        }
                        cast();
                        new PowerShotArrow(archer, points[0], points[1]);
                    }
                };
            }
        });
    }
}