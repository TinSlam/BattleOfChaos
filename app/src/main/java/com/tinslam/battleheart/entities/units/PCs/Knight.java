package com.tinslam.battleheart.entities.units.PCs;

import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.AttackAnimation;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.spells.Spell;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * The class that contains all the Knight entities.
 */
public class Knight extends Pc{
    private static final Object knightsLock = new Object();
    private static ArrayList<Knight> knights = new ArrayList<>();

    /**
     * Constructor.
     * @param x The x position of the Knight.
     * @param y The y position of the Knight.
     */
    public Knight(float x, float y) {
        super(x, y);

        setAnimation(getIdleRightAnimation());

        addKnight(this);
        updateCollisionBox();

        for(String spellClassName : PlayerStats.knightSpells){
            Class<?> spellClass;
            try {
                spellClass = Class.forName(spellClassName);
                Constructor constructor = spellClass.getConstructor(Unit.class);
                Spell spell = (Spell) constructor.newInstance(this);
                addSpell(spell);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the stats of the unit.
     */
    @Override
    public void loadStats() {
        setMaxHp(PlayerStats.knightHp + PlayerStats.knightExtraHp);
        setDamage(PlayerStats.knightDamage + PlayerStats.knightExtraDamage);
        setArmor(PlayerStats.knightArmor + PlayerStats.knightExtraArmor);
        setSpeed(PlayerStats.knightSpeed + PlayerStats.knightExtraSpeed);
        setAttackRange((PlayerStats.knightAttackRange + PlayerStats.knightExtraAttackRange) * GameView.density());
        setAttackCD(PlayerStats.knightAttackCd + PlayerStats.knightExtraAttackCd);
        setAggroRange((int) (PlayerStats.knightAggroRange * GameView.density()));
    }

    /**
     * Reaction to the Knight being attacked. Is called on every hit it receives and survives to tell the tale.
     * Every allied Knight that is in aggro range of the enemy and has a hold state will attack the attacker.
     * Every allied Priest that is in attack range of the unit and has a hold state will heal the unit.
     * @param attacker The attacker unit.
     */
    @Override
    public void reactToBeingAttacked(Unit attacker) {
        if(attacker == null) return;
        if(attacker.getTeam() == getTeam()) return;
        for(Unit x : Unit.getUnits()){
            if(x.getTeam() != getTeam()) continue;
            x.reactToAllyBeingAttacked(this, attacker);
        }
    }

    /**
     * Reacts to an ally being attacked.
     * If in aggro range and state is hold, attacks the attacker.
     * @param ally The ally being attacked.
     * @param attacker The attacker unit.
     */
    @Override
    public void reactToAllyBeingAttacked(Unit ally, Unit attacker) {
        if(attacker == null) return;
        if(!following && Utils.distance(getX(), getY(), attacker.getX(), attacker.getY()) > getAggroRange()) return;
        if(commands.isEmpty() && getTarget() == null){
            commandAttack(attacker);
        }
    }

    /**
     * Checks if the unit is in melee range. (Side by side)
     * @param unit The unit to check for.
     */
    @Override
    public boolean isInAttackRange(Unit unit){
        return Utils.areSideBySide(this, unit);
    }

    /**
     * Initializes the animations.
     */
    @Override
    public void loadAnimations(){
        setIdleRightAnimation(new PerpetualAnimation(AnimationLoader.knightIdleRight, 80, -1, 12 * Image.knightImageScale * GameView.density(), 9 * Image.knightImageScale * GameView.density(), this));
        setMoveRightAnimation(new PerpetualAnimation(AnimationLoader.knightMoveRight, (long) (200 / getSpeed()), -1, 12 * Image.knightImageScale * GameView.density(), 9 * Image.knightImageScale * GameView.density(), this));
        setAttackRightAnimation(new AttackAnimation(AnimationLoader.knightAttackRight, 60, 1, 12 * Image.knightImageScale * GameView.density(), 9 * Image.knightImageScale * GameView.density(), this, getIdleRightAnimation()));

        setMoveLeftAnimation(new PerpetualAnimation(AnimationLoader.knightMoveLeft, (long) (200 / getSpeed()), -1, 30 * Image.knightImageScale * GameView.density(), 9 * Image.knightImageScale * GameView.density(), this));
        setIdleLeftAnimation(new PerpetualAnimation(AnimationLoader.knightIdleLeft, 80, -1, 30 * Image.knightImageScale * GameView.density(), 9 * Image.knightImageScale * GameView.density(), this));
        setAttackLeftAnimation(new AttackAnimation(AnimationLoader.knightAttackLeft, 60, 1, 30 * Image.knightImageScale * GameView.density(), 9 * Image.knightImageScale * GameView.density(), this, getIdleLeftAnimation()));
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
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the collision box according to the image. Must be changed accordingly when a new image is used.
     */
    @Override
    public void updateCollisionBox(){
        getCollisionBox().set((int) getX(), (int) getY(), (int)(getX() + 86 * Image.knightImageScale * GameView.density()), (int) (getY() + 131 * Image.knightImageScale * GameView.density()));
    }

    /**
     * Performs the action to be done by a touch command.
     * Attacks if the target is an enemy.
     */
    @Override
    public void performAction() {
        if(getTarget() != null){
            if(getTarget().getTeam() == Consts.TEAM_ALLIED || getTarget().getTeam() == Consts.TEAM_NEUTRAL){
                setTarget(null);
                return;
            }
            commandAttack(getTarget());
        }
    }

    /**
     * A method that decides which unit must be prioritized to be chosen when a spot on the screen is touched that contains multiple units overlapped at it.
     * The chosen unit is the first enemy unit on the list.
     * @param units An ArrayList of all the overlapped units.
     * @return The chosen unit.
     */
    @Override
    public Unit choosePriorityUnit(ArrayList<Unit> units) {
        Unit unit = null;
        while(!units.isEmpty()){
            unit = units.get(units.size() - 1);
            units.remove(unit);
            if(unit.getTeam() == Consts.TEAM_ENEMY){
                return unit;
            }
        }
        return unit;
    }

    /**
     * Destroys the Knight. Removes it from the Knights list.
     */
    @Override
    public void destroyPc() {
        removeKnight(this);
    }

    /**
     * Ticks the Knight.
     */
    @Override
    public void tickPc() {

    }

    /**
     * Renders the Knight.
     * Drawing the animation.
     */
    @Override
    public void renderPc(float xOffset, float yOffset) {
        if(getAnimation() != null){
            getAnimation().render(getX() + xOffset, getY() + yOffset);
        }
    }

    /**
     * Adds the Knight to the knights list.
     */
    private static void addKnight(final Knight knight){
        new Event() {
            @Override
            public void performAction() {
                    getKnights().add(knight);
            }
        };
    }

    /**
     * Removes the Knight from the Knights list.
     */
    private static void removeKnight(final Knight knight){
        new Event() {
            @Override
            public void performAction() {
                    knights.remove(knight);
            }
        };
    }

    /**
     *
     * @return A lock that controls the synchronization of the actions done on the Knights list.
     */
    private static Object getKnightsLock() {
        return knightsLock;
    }

    /**
     *
     * @return An ArrayList of all the existing Knights.
     */
    private static ArrayList<Knight> getKnights() {
        return knights;
    }
}