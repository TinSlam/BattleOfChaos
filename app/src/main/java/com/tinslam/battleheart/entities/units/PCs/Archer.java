package com.tinslam.battleheart.entities.units.PCs;

import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.UI.graphics.Animations.ShootAnimation;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.projectiles.Arrow;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.spells.Spell;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * The class that contains all the Archer entities.
 */
public class Archer extends Pc{
    private static final Object archersLock = new Object();
    private static ArrayList<Archer> archers = new ArrayList<>();

    /**
     * Constructor.
     * @param x The x position of the Archer.
     * @param y The y position of the Archer.
     */
    public Archer(float x, float y) {
        super(x, y);

        setImage(getMoveRightAnimation());

        addArcher(this);
        updateCollisionBox();

        for(String spellClassName : PlayerStats.archerSpells){
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
        setMaxHp(PlayerStats.archerHp + PlayerStats.archerExtraHp);
        setDamage(PlayerStats.archerDamage + PlayerStats.archerExtraDamage);
        setArmor(PlayerStats.archerArmor + PlayerStats.archerExtraArmor);
        setSpeed(PlayerStats.archerSpeed + PlayerStats.archerExtraSpeed);
        setAttackRange((PlayerStats.archerAttackRange + PlayerStats.archerExtraAttackRange) * GameView.density());
        setAttackCD(PlayerStats.archerAttackCd + PlayerStats.archerExtraAttackCd);
        setAggroRange((int) (PlayerStats.archerAggroRange * GameView.density()));
    }

    /**
     * Reaction to the Archer being attacked. Is called on every hit it receives and survives to tell the tale.
     * Every allied Archer that is in aggro range of the enemy and has a hold state will attack the attacker.
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
     * Reaction to an ally being attacked.
     * If enemy in attack range and the state is hold then attacks the attacker.
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
     * Checks if the unit is in range.
     * @param unit The unit to check for.
     */
    @Override
    public boolean isInAttackRange(Unit unit){
        if(unit == null) return false;
        return Utils.isInCircle(unit.getCollisionBox().centerX(), unit.getCollisionBox().centerY(), getCollisionBox().centerX(), getCollisionBox().centerY(), getAttackRange());
    }

    /**
     * Initializes the animations.
     */
    @Override
    public void loadAnimations(){
        setMoveRightAnimation(new PerpetualAnimation(AnimationLoader.archerMoveRight, (long) (200 / getSpeed()), -1, 0, 0, this));
        setMoveLeftAnimation(new PerpetualAnimation(AnimationLoader.archerMoveLeft, (long) (200 / getSpeed()), -1, 0, 0, this));
        setAttackRightAnimation(new ShootAnimation(Arrow.class, AnimationLoader.archerAttackRight, 120, 1, 0, 0, this, getMoveRightAnimation()));
        setAttackLeftAnimation(new ShootAnimation(Arrow.class, AnimationLoader.archerAttackLeft, 120, 1, 0, 0, this, getMoveLeftAnimation()));
    }

    /**
     * Updates the collision box according to the image. Must be changed accordingly when a new image is used.
     */
    @Override
    public void updateCollisionBox(){
        if(getCollisionBox() != null && getAnimation() != null) getCollisionBox().set((int) getX(), (int) getY(), (int)(getX() + getAnimation().getWidth()), (int) (getY() + getAnimation().getHeight()));
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
     * Has the unit move in range of its target. Is called automatically on every frame if the unit is on attacking state. Must be overrode for different approaches.
     * Moves towards the target until is in heal range.
     */
    @Override
    public boolean initTargetLocation(){
        setX2(getTarget().getCollisionBox().centerX());
        setY2(getTarget().getCollisionBox().centerY());
        return true;
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
     * Destroys the Archer. Removes it from the Archers list.
     */
    @Override
    public void destroyPc() {
        removeArcher(this);
    }

    /**
     * Ticks the Archer.
     */
    @Override
    public void tickPc() {

    }

    /**
     * Renders the Archer.
     * Drawing the animation.
     */
    @Override
    public void renderPc(float xOffset, float yOffset) {
        if(getAnimation() != null) getAnimation().render(getX() + xOffset, getY() + yOffset);
    }

    /**
     * Adds the Archer to the archers list.
     */
    private static void addArcher(final Archer archer){
        new Event() {
            @Override
            public void performAction() {
                getArchers().add(archer);
            }
        };
    }

    /**
     * Removes the Archer from the Archers list.
     */
    private static void removeArcher(final Archer archer){
        new Event() {
            @Override
            public void performAction() {
                    archers.remove(archer);
            }
        };
    }

    /**
     *
     * @return A lock that controls the synchronization of the actions done on the Archers list.
     */
    private static Object getArchersLock() {
        return archersLock;
    }

    /**
     *
     * @return An ArrayList of all the existing Archers.
     */
    private static ArrayList<Archer> getArchers() {
        return archers;
    }
}