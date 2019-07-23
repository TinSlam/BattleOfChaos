package com.tinslam.battleheart.spells;

import android.graphics.Bitmap;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.utils.constants.SpellConsts;

/**
 * A class for the Aoe heal spell.
 */
public class HealAOE extends Spell{
    private static Texture staticImage = new Texture(TextureData.portrait_heal_aoe, SpellConsts.PORTRAIT_WIDTH, SpellConsts.PORTRAIT_HEIGHT);

    /**
     * Constructor.
     */
    public HealAOE(Unit caster) {
        super(caster, staticImage);

        setName(GameView.string(R.string.spell_aoe_heal));
    }

    /**
     * Casts the spell.
     */
    @Override
    public void castSpell(){
        for(Unit x : Unit.getUnits()){
            if(x.getTeam() == caster.getTeam()){
                x.damage(-SpellConsts.HEAL_AOE_AMOUNT, caster);
            }
        }
    }

    /**
     * Is called when spell button is clicked.
     */
    @Override
    public void onClick() {

    }
}
