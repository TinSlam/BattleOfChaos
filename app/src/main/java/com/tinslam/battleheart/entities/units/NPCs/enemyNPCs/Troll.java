package com.tinslam.battleheart.entities.units.NPCs.enemyNPCs;

import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.AttackAnimation;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.AI;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

/**
 * The class that contains all the existing Trolls :D
 */
public class Troll extends EnemyNpc{
    private static final Object trollsLock = new Object();
    private static ArrayList<Troll> trolls = new ArrayList<>();

    /**
     * Constructor.
     * @param x The x position of the Troll.
     * @param y The y position of the Troll.
     */
    public Troll(float x, float y) {
        super(x, y);


        setImage(getMoveRightAnimation());
        setAttackRange(4 * GameView.density());

        addTroll(this);
        updateCollisionBox();
    }

    /**
     * The AI behaviour of the Npc. Is called on every frame.
     * Looks for targets if there is none. Attacks if it has a target.
     */
    @Override
    public void ai() {
        if(getTarget() == null){
            Unit unit = AI.findTarget(this, true);
            if(unit != null){
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
        return Utils.areSideBySide(this, unit);
    }

    /**
     * Destroys the Troll. Removes it from the Trolls list.
     */
    @Override
    public void destroyEnemyNpc() {
        removeTroll(this);
    }

    /**
     * Ticks the Troll.
     */
    @Override
    public void tickEnemyNpc() {

    }

    /**
     * Renders the Troll.
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
        getCollisionBox().set((int) getX(), (int) getY(), (int) (getX() + 32 * GameView.density()), (int) (getY() + 84 * GameView.density()));
    }

    /**
     * Initializes the animations.
     */
    @Override
    public void loadAnimations() {
        setMoveLeftAnimation(new PerpetualAnimation(AnimationLoader.trollMoveLeft, (long) (400 / getSpeed()), -1, 42 * GameView.density(), 16 * GameView.density(), this));
        setMoveRightAnimation(new PerpetualAnimation(AnimationLoader.trollMoveRight, (long) (400 / getSpeed()), -1, 46 * GameView.density(), 16 * GameView.density(), this));
        setAttackRightAnimation(new AttackAnimation(AnimationLoader.trollAttackRight, 120, 1, 28 * GameView.density(), 20 * GameView.density(), this, getMoveRightAnimation()));
        setAttackLeftAnimation(new AttackAnimation(AnimationLoader.trollAttackLeft, 120, 1, 30 * GameView.density(), 20 * GameView.density(), this, getMoveLeftAnimation()));
    }

    /**
     * Follows the target until gets in melee range.
     */
    @Override
    public boolean initTargetLocation() {
        setX2((getCollisionBox().centerX() < getTarget().getCollisionBox().centerX()) ? getTarget().getCollisionBox().left - getAttackRange() - getCollisionBox().width() : getTarget().getCollisionBox().right + getAttackRange());
        setY2(getTarget().getCollisionBox().bottom - getCollisionBox().height());
        if(!BattleState.getBattleState().canMoveToDestination(this)){
            setX2((getCollisionBox().centerX() >= getTarget().getCollisionBox().centerX()) ? getTarget().getCollisionBox().left - getAttackRange() - getCollisionBox().width() : getTarget().getCollisionBox().right + getAttackRange());
            if(!BattleState.getBattleState().canMoveToDestination(this)){
                Unit unit = AI.findTarget(this, true);
                if(unit == null){
                    return false;
                }else{
                    setTarget(unit);
                    initTargetLocation();
                }
            }
        }
        return true;
    }

    /**
     * Adds the Troll to the Trolls list.
     */
    private static void addTroll(final Troll troll){
        new Event() {
            @Override
            public void performAction() {
                trolls.add(troll);
            }
        };
    }

    /**
     * Removes the Troll from the Trolls list.
     */
    private static void removeTroll(final Troll troll){
        new Event() {
            @Override
            public void performAction() {
                trolls.remove(troll);
            }
        };
    }
}