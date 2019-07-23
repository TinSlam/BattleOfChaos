package com.tinslam.battleheart.entities.units.PCs;

import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.AttackAnimation;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.UI.graphics.visualEffects.attachedToUnitVisualEffects.VisualEffectHeal;
import com.tinslam.battleheart.base.GameView;
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
 * The class that contains all the Priest entities.
 */
public class Priest extends Pc{
    private static final Object priestsLock = new Object();
    private static ArrayList<Priest> priests = new ArrayList<>();

    /**
     * Constructor.
     * @param x The x position of the Priest.
     * @param y The y position of the Priest.
     */
    public Priest(float x, float y) {
        super(x, y);

        setImage(getMoveRightAnimation());

        addPriest(this);
        updateCollisionBox();

        for(String spellClassName : PlayerStats.priestSpells){
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
     * Loads the stats of the unit.
     */
    @Override
    public void loadStats() {
        setMaxHp(PlayerStats.priestHp + PlayerStats.priestExtraHp);
        setDamage(PlayerStats.priestDamage + PlayerStats.priestExtraDamage);
        setArmor(PlayerStats.priestArmor + PlayerStats.priestExtraArmor);
        setSpeed(PlayerStats.priestSpeed + PlayerStats.priestExtraSpeed);
        setAttackRange((PlayerStats.priestAttackRange + PlayerStats.priestExtraAttackRange) * GameView.density());
        setAttackCD(PlayerStats.priestAttackCd + PlayerStats.priestExtraAttackCd);
        setAggroRange((int) (PlayerStats.priestAggroRange * GameView.density()));
    }

    /**
     * Reaction to the Priest being attacked. Is called on every hit it receives and survives to tell the tale.
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
     * If in aggro range of the ally and state is hold will start healing the ally.
     * @param ally The ally being attacked.
     * @param attacker The attacker unit.
     */
    @Override
    public void reactToAllyBeingAttacked(Unit ally, Unit attacker) {
        if(attacker == null) return;
        if(ally == null) return;
        if(!following && Utils.distance(getX(), getY(), ally.getX(), ally.getY()) > getAttackRange()) return;
        if(commands.isEmpty() && getTarget() == null){
            commandAttack(ally);
        }
    }

    /**
     * Checks if the unit is in healing range.
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
        setMoveRightAnimation(new PerpetualAnimation(AnimationLoader.priestMoveRight, 1000000, -1, 22 * Image.priestImageScale * GameView.density(), 20 * Image.priestImageScale * GameView.density(), this));
        setMoveLeftAnimation(new PerpetualAnimation(AnimationLoader.priestMoveLeft, 1000000, -1, 19 * Image.priestImageScale * GameView.density(), 20 * Image.priestImageScale * GameView.density(), this));
        setAttackRightAnimation(new AttackAnimation(AnimationLoader.priestAttackRight, 120, 1, 22 * Image.priestImageScale * GameView.density(), 20 * Image.priestImageScale * GameView.density(), this, getMoveRightAnimation()){
            @Override
            public void extraEffects(){
                if(getTarget() != null) new VisualEffectHeal(getTarget());
            }
        });
        setAttackLeftAnimation(new AttackAnimation(AnimationLoader.priestAttackLeft, 120, 1, 19 * Image.priestImageScale * GameView.density(), 20 * Image.priestImageScale * GameView.density(), this, getMoveLeftAnimation()){
            @Override
            public void extraEffects(){
                if(getTarget() != null) new VisualEffectHeal(getTarget());
            }
        });
    }

    /**
     * Updates the collision box according to the image. Must be changed accordingly when a new image is used.
     */
    @Override
    public void updateCollisionBox(){
        getCollisionBox().set((int) getX(), (int) getY(), (int)(getX() + 23 * Image.priestImageScale * GameView.density()), (int) (getY() + 40 * Image.priestImageScale * GameView.density()));
    }

    /**
     * Performs the action to be done by a touch command.
     * Heals if the target is an ally.
     */
    @Override
    public void performAction() {
        if(getTarget() != null){
            if(getTarget().getTeam() == Consts.TEAM_ENEMY || getTarget().getTeam() == Consts.TEAM_NEUTRAL){
                setTarget(null);
                return;
            }
            commandAttack(getTarget());
        }
    }

    /**
     * A method that decides which unit must be prioritized to be chosen when a spot on the screen is touched that contains multiple units overlapped at it.
     * The chosen unit is the first allied unit on the list.
     * @param units An ArrayList of all the overlapped units.
     * @return The chosen unit.
     */
    @Override
    public Unit choosePriorityUnit(ArrayList<Unit> units) {
        Unit unit = null;
        while(!units.isEmpty()){
            unit = units.get(units.size() - 1);
            units.remove(unit);
            if(unit.getTeam() == Consts.TEAM_ALLIED){
                return unit;
            }
        }
        return unit;
    }

    /**
     * Destroys the Priest. Removes it from the Priests list.
     */
    @Override
    public void destroyPc() {
        removePriest(this);
    }

    /**
     * Ticks the Priest.
     */
    @Override
    public void tickPc() {

    }

    /**
     * Renders the Priest.
     * Drawing the animation.
     */
    @Override
    public void renderPc(float xOffset, float yOffset) {
        if(getAnimation() != null) getAnimation().render(getX() + xOffset, getY() + yOffset);
    }

    /**
     * Adds the Priest to the Priests list.
     */
    private static void addPriest(final Priest priest){
        getPriests().add(priest);
    }

    /**
     * Removes the Priest from the Priests list.
     */
    private static void removePriest(final Priest priest){
        new Event() {
            @Override
            public void performAction() {
                    priests.remove(priest);
            }
        };
    }

    /**
     *
     * @return A lock that controls the synchronization of the actions done on the Priests list.
     */
    private static Object getPriestsLock() {
        return priestsLock;
    }

    /**
     *
     * @return An ArrayList of all the existing Priests.
     */
    private static ArrayList<Priest> getPriests() {
        return priests;
    }
}
