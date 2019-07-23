package com.tinslam.battleheart.entities.units.NPCs.enemyNPCs;

import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.UI.graphics.Animations.ShootAnimation;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.projectiles.Arrow;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.AI;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.states.ArenaState;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

/**
 * The class that contains all the MummyArchers.
 */
public class MummyArcher extends EnemyNpc{
    private static final Object mummyArchersLock = new Object();
    private static ArrayList<MummyArcher> mummyArchers = new ArrayList<>();

    /**
     * Constructor.
     * @param x The x position of the MummyArcher.
     * @param y The y position of the MummyArcher.
     */
    public MummyArcher(float x, float y) {
        super(x, y);

        setImage(getMoveRightAnimation());
        setAttackRange(200 * GameView.density());
        setAttackCD(1200);
        setProjectileSpeed(20);

        addMummyArcher(this);
        updateCollisionBox();
    }

    /**
     * The AI behaviour of the Npc. Is called on every frame.
     * Looks for targets if there is none. Attacks if it has a target.
     */
    @Override
    public void ai() {
        if(getTarget() == null){
            if(GameView.getState() instanceof ArenaState){
                if(!ArenaState.getBattleState().getMapRect().contains(getCollisionBox())){
                    commandMove(ArenaState.getBattleState().getMapRect().centerX(), ArenaState.getBattleState().getMapRect().centerY());
                    return;
                }else{
                    resetCommands();
                }
            }
            Unit unit = AI.findTarget(this);
            if(unit != null) {
                commandAttack(unit);
            }else{
                if(BattleState.getBattleState().isInCamera(this)){
                    if(isOnGuard()){
                        if(Utils.distance(this, getGuardX(), getGuardY()) > getGuardRadius() / 5){
                            commandMove(getGuardX(), getGuardY());
                        }else{
                            resetCommands();
                        }
                    }
                }
            }
        }else{
            if(commands.isEmpty()) commandAttack(getTarget());
        }
    }

    /**
     * Loads the stats of the unit.
     */
    @Override
    public void loadStats() {

    }

    /**
     * This method is triggered every time the unit gets attacked and its hp doesn't drop to 0 or less.
     * @param attacker The attacker unit.
     */
    @Override
    public void reactToBeingAttacked(Unit attacker) {
        if(attacker == null) return;
        if(attacker.getTeam() == getTeam()) return;
        float newTargetDistance = Utils.distance(getCollisionBox().centerX(), getCollisionBox().centerY(),
                attacker.getCollisionBox().centerX(), attacker.getCollisionBox().centerY());
        float oldTargetDistance = Float.MAX_VALUE;
        if(getTarget() != null){
            oldTargetDistance = Utils.distance(getCollisionBox().centerX(), getCollisionBox().centerY(),
                    getTarget().getCollisionBox().centerX(), getTarget().getCollisionBox().centerY());
        }
        if(newTargetDistance < oldTargetDistance) commandAttack(attacker);
    }

    /**
     * Reacts to an ally being attacked.
     * @param ally The ally being attacked.
     * @param attacker The attacker unit.
     */
    @Override
    public void reactToAllyBeingAttacked(Unit ally, Unit attacker) {

    }

    /**
     * Checks if the unit is in melee range. (Side by side)
     * @param unit The unit to check for.
     */
    @Override
    public boolean isInAttackRange(Unit unit) {
        if(unit == null) return false;
        return Utils.isInCircle(unit.getCollisionBox().centerX(), unit.getCollisionBox().centerY(), getCollisionBox().centerX(), getCollisionBox().centerY(), getAttackRange());
    }

    /**
     * Destroys the MummyArcher. Removes it from the MummyArchers list.
     */
    @Override
    public void destroyEnemyNpc() {
        removeMummyArcher(this);
    }

    /**
     * Ticks the MummyArcher.
     */
    @Override
    public void tickEnemyNpc() {

    }

    /**
     * Renders the MummyArcher.
     * Draws the animation.
     */
    @Override
    public void renderEnemyNpc(float xOffset, float yOffset) {
        if(getAnimation() != null) getAnimation().render(getX() + xOffset, getY() + yOffset);
    }

    /**
     * Updates the collision box according to the image. Must be changed accordingly when a new image is used.
     */
    @Override
    public void updateCollisionBox(){
        if(getAnimation() != null) getCollisionBox().set((int) getX(), (int) getY(), (int)(getX() + getAnimation().getWidth()), (int) (getY() + getAnimation().getHeight()));
    }

    /**
     * Initializes the animations.
     */
    @Override
    public void loadAnimations() {
        setMoveLeftAnimation(new PerpetualAnimation(AnimationLoader.mummyArcherMoveLeft, (long) (400 / getSpeed()), -1, 0, 0, this));
        setMoveRightAnimation(new PerpetualAnimation(AnimationLoader.mummyArcherMoveRight, (long) (400 / getSpeed()), -1, 0, 0, this));
        setAttackRightAnimation(new ShootAnimation(Arrow.class, AnimationLoader.mummyArcherAttackRight, 120, 1, 0, 0, this, getMoveRightAnimation()));
        setAttackLeftAnimation(new ShootAnimation(Arrow.class, AnimationLoader.mummyArcherAttackLeft, 120, 1, 0, 0, this, getMoveLeftAnimation()));
    }

    /**
     * Follows the target until gets in melee range.
     */
    @Override
    public boolean initTargetLocation() {
        setX2(getTarget().getCollisionBox().centerX());
        setY2(getTarget().getCollisionBox().centerY());
        return true;
    }

    /**
     * Adds the MummyArcher to the MummyArchers list.
     */
    private static void addMummyArcher(final MummyArcher ma){
        new Event() {
            @Override
            public void performAction() {
                    mummyArchers.add(ma);
            }
        };
    }

    /**
     * Removes the MummyArcher from the MummyArchers list.
     */
    private static void removeMummyArcher(final MummyArcher ma){
        new Event() {
            @Override
            public void performAction() {
                mummyArchers.remove(ma);
            }
        };
    }
}