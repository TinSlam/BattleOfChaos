package com.tinslam.battleheart.gameUtility;

import com.tinslam.battleheart.entities.units.NPCs.Npc;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;

/**
 * The class that contains AI commands.
 */
public class AI{
    /**
     * Finds the closest enemy to a certain unit.
     * @param unit The unit to run the code for.
     * @return The closest enemy to the unit.
     */
    public static Unit findTarget(Unit unit){
        return findTarget(unit, false);
    }

    /**
     * Finds the closest enemy to a certain unit.
     * @param unit The unit to run the code for.
     * @return The closest enemy to the unit.
     */
    public static Unit findTarget(Unit unit, boolean melee){
        if(unit == null) return null;
        Unit target = null;
        float inGameDistance = 0;
        if(unit instanceof Npc){
            for(Pc x : Pc.getPcs()){
//                    if(x.getTeam() == unit.getTeam() || x.getTeam() == Consts.TEAM_NEUTRAL) continue;
                boolean meleeFlag = false;
                if(melee){
                    unit.setX2((unit.getCollisionBox().centerX() < x.getCollisionBox().centerX()) ? x.getCollisionBox().left - unit.getAttackRange() - unit.getCollisionBox().width() : x.getCollisionBox().right + unit.getAttackRange());
                    unit.setY2(x.getCollisionBox().bottom - unit.getCollisionBox().height());
                    if(!BattleState.getBattleState().canMoveToDestination(unit)){
                        unit.setX2((unit.getCollisionBox().centerX() >= x.getCollisionBox().centerX()) ? x.getCollisionBox().left - unit.getAttackRange() - unit.getCollisionBox().width() : x.getCollisionBox().right + unit.getAttackRange());
                        if(BattleState.getBattleState().canMoveToDestination(unit)){
                            meleeFlag = true;
                        }
                    }else{
                        meleeFlag = true;
                    }
                }
                if(target == null){
                    if(!melee || meleeFlag){
                        target = x;
                        inGameDistance = Utils.distance(unit.getX(), unit.getY(), target.getX(), target.getY());
                        continue;
                    }
                }
                if(Utils.distance(unit.getX(), unit.getY(), x.getX(), x.getY()) < inGameDistance){
                    if(!melee || meleeFlag){
                        target = x;
                        inGameDistance = Utils.distance(unit.getX(), unit.getY(), target.getX(), target.getY());
                    }
                }
            }
        }else if(unit instanceof Pc){
            for(Npc x : Npc.getNpcs()){
                if(x.getTeam() == Consts.TEAM_NEUTRAL) continue;
                boolean meleeFlag = false;
                if(melee){
                    unit.setX2((unit.getCollisionBox().centerX() < x.getCollisionBox().centerX()) ? x.getCollisionBox().left - unit.getAttackRange() - unit.getCollisionBox().width() : x.getCollisionBox().right + unit.getAttackRange());
                    unit.setY2(x.getCollisionBox().bottom - unit.getCollisionBox().height());
                    if(!BattleState.getBattleState().canMoveToDestination(unit)){
                        unit.setX2((unit.getCollisionBox().centerX() >= x.getCollisionBox().centerX()) ? x.getCollisionBox().left - unit.getAttackRange() - unit.getCollisionBox().width() : x.getCollisionBox().right + unit.getAttackRange());
                        if(BattleState.getBattleState().canMoveToDestination(unit)){
                            meleeFlag = true;
                        }
                    }else{
                        meleeFlag = true;
                    }
                }
                if(target == null){
                    if(!melee || meleeFlag){
                        target = x;
                        inGameDistance = Utils.distance(unit.getX(), unit.getY(), target.getX(), target.getY());
                        continue;
                    }
                }
                if(Utils.distance(unit.getX(), unit.getY(), x.getX(), x.getY()) < inGameDistance){
                    if(!melee || meleeFlag){
                        target = x;
                        inGameDistance = Utils.distance(unit.getX(), unit.getY(), target.getX(), target.getY());
                    }
                }
            }
        }
        if(target == null) return null;
        inGameDistance = Utils.distance(target.getCollisionBox().centerX(), target.getCollisionBox().centerY(),
                unit.getGuardX(), unit.getGuardY());
        if(unit.isOnGuard()){
            if(inGameDistance > unit.getGuardRadius()) target = null;
        }
        return target;
    }
}
