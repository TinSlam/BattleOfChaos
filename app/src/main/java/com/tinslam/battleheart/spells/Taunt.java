package com.tinslam.battleheart.spells;

import android.graphics.Bitmap;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.UI.graphics.visualEffects.attachedToUnitVisualEffects.VisualEffectTaunt;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses.Boss;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.Timer;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.SpellConsts;

/**
 * A class for the Taunt spell.
 */
public class Taunt extends Spell{
    private static Texture staticImage = new Texture(TextureData.portrait_taunt, SpellConsts.PORTRAIT_WIDTH, SpellConsts.PORTRAIT_HEIGHT);

    /**
     * Constructor.
     */
    public Taunt(Unit caster) {
        super(caster, staticImage);

        setName(GameView.string(R.string.spell_taunt));
    }

    /**
     * Casts the spell.
     */
    @Override
    public void castSpell(){
        if(caster == null) return;
        for(Unit x : Unit.getUnits()){
            if(x.getTeam() != caster.getTeam() && x.getTeam() != Consts.TEAM_NEUTRAL){
                if(x instanceof Boss && !((Boss) x).isTriggered()) continue;
                if(Utils.distance(caster.getCollisionBox().centerX(), caster.getCollisionBox().centerY(), x.getCollisionBox().centerX(), x.getCollisionBox().centerY())
                        > SpellConsts.TAUNT_RADIUS * GameView.density()) continue;
                x.commandAttack(caster);
                new VisualEffectTaunt(x);
            }
        }
        caster.setSpeed(caster.getRealSpeed() + SpellConsts.TAUNT_BONUS_MS);
        new TimedTask(SpellConsts.TAUNT_TIME) {
            @Override
            public void performAction() {
                caster.setSpeed(caster.getRealSpeed() - SpellConsts.TAUNT_BONUS_MS);
            }
        };
//        new TimedTaskRepeat(100) {
//            private int counter = 0;
//
//            @Override
//            public boolean checkCondition() {
//                return !(counter < SpellConsts.TAUNT_TIME);
//            }
//
//            @Override
//            public void performAction() {
//                if(GameView.isActive()) counter += 100;
//            }
//
//            @Override
//            public void end() {
//                counter = 0;
//                new TimedTaskRepeat(100) {
//                    @Override
//                    public boolean checkCondition() {
//                        return !(counter < spellsCastedWhileOnCooldown * SpellConsts.SLOW_MO_TIME);
//                    }
//
//                    @Override
//                    public void performAction() {
//                        if(GameView.isActive()) counter += 100;
//                    }
//
//                    @Override
//                    public void end() {
//                        caster.setSpeed(caster.getRealSpeed() - SpellConsts.TAUNT_BONUS_MS);
//                    }
//                };
//            }
//        };
    }

    /**
     * Is called when spell button is clicked.
     */
    @Override
    public void onClick() {

    }
}