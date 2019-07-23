package com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses;

import android.graphics.Rect;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.AttackAnimation;
import com.tinslam.battleheart.UI.graphics.Animations.CastAnimation;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.entities.projectiles.GiantTrollAxe;
import com.tinslam.battleheart.entities.projectiles.GiantTrollFallingRock;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.AI;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.interfaces.ActionInterface;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.states.ReplayState;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;

public class GiantTroll extends Boss{
    private float stompDamage = 100;
    private float axeDamage = 60;
    private float rockDamage = 15;

    private static float sizeScale = 1.5f;

    private int phase = 1;
    private int phaseTwoDuration = GameThread.maxFps * 10;
    private int phaseCounter = 0;

    private boolean stompReady = false;
    private boolean throwAxeReady = false;
    private int stompCD = GameThread.maxFps * 6;
    private int throwAxeCD = GameThread.maxFps * 3;
    private int stompCounter = 0;
    private int throwAxeCounter = 0;
    private int spawnRockInterval = GameThread.maxFps / 2;
    private int spawnRockCounter = 0;

    private int stompRadius = (int) (192 * GameView.density());
    private float stompX, stompY;

    private byte stompAnimationStage = 0;
    private int[] stompImages = Utils.resizeAnimation(AnimationLoader.sunStrike, 2 * stompRadius, stompRadius);
    private GiantTroll boss;
    private Animation stompAnimation = new Animation(stompImages, 250, 1, 0, 0) {

        @Override
        public void initAnimation() {

        }

        @Override
        public void halfWay() {
            for(Pc pc : Pc.getPcs()){
                if(Utils.rectCollidesOval(pc.getCollisionBox(), stompX, stompY, stompRadius * 4 / 5, stompRadius / 2 * 4 / 5)){
                    pc.damage(stompDamage, boss);
                }
            }
        }

        @Override
        public void extraEffects() {

        }

        @Override
        public void finished() {
            stompAnimationStage = 0;
        }

        @Override
        public void onEnd() {

        }

        @Override
        public void onCycleEnd() {

        }
    };

    private CastAnimation stompLeftCastAnimation, stompRightCastAnimation;

    /**
     * Constructor.
     *
     * @param x        The x position of the Npc.
     * @param y        The y position of the Npc.
     */
    public GiantTroll(float x, float y, Rect bossArea) {
        super(x, y, bossArea);

        constructor();
    }

    public GiantTroll(float x, float y, Rect bossArea, int seed) {
        super(x, y, bossArea, seed);

        constructor();
    }

    private void constructor(){
        boss = this;
        setImage(getMoveRightAnimation());
        setAttackRange(4 * GameView.density());

        updateCollisionBox();
    }

    @Override
    public void reset(){
        phase = 1;
        damage(-getMaxHp(), this);
        triggered = false;
        if(!BattleState.getBattleState().isOver()) DungeonState.getDungeonState().stopScreenShaking();
        setImage(getMoveRightAnimation());
        setPosition(spawnX, spawnY);
        phaseCounter = 0;
        throwAxeCounter = 0;
        stompCounter = 0;
        spawnRockCounter = 0;
        stompReady = false;
        throwAxeReady = false;
        stompAnimationStage = 0;
    }

    @Override
    public void damage(float damage, Unit attacker){
        if(!triggered || phase == 2) return;
        super.damage(damage, attacker);

        if(phase == 1 && getHp() <= getMaxHp() / 2){
            phase = 2;
            if(!commands.isEmpty()){
                resetCommands();
            }
            phaseCounter = 0;
            if(BattleState.getBattleState() instanceof DungeonState){
                DungeonState.getDungeonState().shakeScreen();
            }else{
                ReplayState.getReplayState().shakeScreen();
            }
        }
    }

    /**
     * The AI behaviour of the Npc. Is called on every frame.
     */
    @Override
    public void ai() {
        if(triggered){
            if(getAnimation() == stompLeftCastAnimation || getAnimation() == stompRightCastAnimation) return;
            switch(phase){
                case 1 :
                case 3 :
                    if(stompReady){
                        stomp();
                    }else if(throwAxeReady){
                        throwAxe();
                    }else{
                        physicalAttack();
                    }
                    break;

                case 2 :
                    spawnRocks();
                    break;
            }
        }
    }

    private void stomp(){
        if(getAnimation() == getMoveRightAnimation()){
            stompReady = false;
            commandCast(stompRightCastAnimation);
        }else if(getAnimation() == getMoveLeftAnimation()){
            stompReady = false;
            commandCast(stompLeftCastAnimation);
        }
    }

    private void throwAxe(){
        throwAxeReady = false;
        final int index;
        final int x, y;
        if(Pc.getPcs().isEmpty()) return;
        index = Utils.getRandomIntegerInTheRange(0, Pc.getPcs().size() - 1, random);
        x = Pc.getPcs().get(index).getCollisionBox().centerX();
        y = Pc.getPcs().get(index).getCollisionBox().centerY();
        new Event() {
            @Override
            public void performAction() {
                new GiantTrollAxe(getCollisionBox().left, getCollisionBox().top + getCollisionBox().height() / 4,
                        getCollisionBox().height() * 5 / 2, getCollisionBox().height() / 2,
                        x, y, boss);
            }
        };
    }

    private void spawnRocks(){
        if(spawnRockCounter >= spawnRockInterval){
            spawnRockCounter = 0;
            for(int i = 0; i < 1; i++){
                final int x = Utils.getRandomIntegerInTheRange(bossArea.left, (int) (bossArea.right - 64 * GameView.density()), random);
                final int y = Utils.getRandomIntegerInTheRange(bossArea.top, (int) (bossArea.bottom - 64 * GameView.density()), random);
                new Event() {
                    @Override
                    public void performAction() {
                        new GiantTrollFallingRock(x, bossArea.top - 128 * GameView.density(), x, y, boss);
                    }
                };
            }
        }
        spawnRockCounter++;
    }

    private void physicalAttack(){
        if(getState() == Consts.STATE_CAST) return;
        if(getTarget() == null){
            Unit unit = AI.findTarget(this, true);
            if(unit != null){
                commandAttack(unit);
            }else{
                if(isOnGuard()){
                    commandMove(getGuardX(), getGuardY());
                }
            }
        }else{
            if(commands.isEmpty()) commandAttack(getTarget());
        }
    }

    @Override
    public void tickBoss() {
        if(triggered){
            if(phase == 2){
                phaseCounter++;
                if(phaseCounter == phaseTwoDuration){
                    phase = 3;
                    throwAxeCD /= 2;
                    throwAxeReady = false;
                    stompReady = false;
                    if(BattleState.getBattleState() instanceof DungeonState){
                        DungeonState.getDungeonState().stopScreenShaking();
                    }else{
                        ReplayState.getReplayState().stopScreenShaking();
                    }
                }
            }else{
                if(!stompReady){
                    stompCounter++;
                    if(stompCounter >= stompCD){
                        stompCounter = 0;
                        stompReady = true;
                    }
                }
                if(!throwAxeReady){
                    throwAxeCounter++;
                    if(throwAxeCounter >= throwAxeCD){
                        throwAxeCounter = 0;
                        throwAxeReady = true;
                    }
                }
            }
        }
    }

    @Override
    public void renderBoss(float xOffset, float yOffset) {
        if(stompAnimationStage == 2){
            stompAnimation.render(xOffset + stompX - stompRadius, yOffset + stompY - stompRadius / 2);
        }else if(stompAnimationStage == 1){
            if(getAnimation() == stompRightCastAnimation || getAnimation() == stompLeftCastAnimation)
                MyGL2dRenderer.drawLabel(xOffset + getSolidBox().centerX() - stompRadius, yOffset + getSolidBox().centerY() - stompRadius / 2,
                    2 * stompRadius, stompRadius, TextureData.sun_strike_visual_indicator, 255);
        }
        if(getAnimation() != null){
            if(phase == 2){
                getAnimation().render(getX() + xOffset, getY() + yOffset, 127);
            }else{
                getAnimation().render(getX() + xOffset, getY() + yOffset);
            }
        }
    }

    /**
     * Updates the collision box according to the image. Must be changed accordingly when a new image is used.
     */
    @Override
    public void updateCollisionBox(){
        getCollisionBox().set((int) getX(), (int) getY(), (int) (getX() + 32 * GameView.density() * sizeScale), (int) (getY() + 84 * GameView.density() * sizeScale));
    }

    @Override
    public void destroyBoss() {
        if(!BattleState.getBattleState().isOver()){
            if(BattleState.getBattleState() instanceof DungeonState){
                DungeonState.getDungeonState().stopScreenShaking();
            }else{
                ReplayState.getReplayState().stopScreenShaking();
            }
        }
        if(BattleState.getBattleState() instanceof ReplayState) return;
        if(!BattleState.getBattleState().isOver()){
            final int respawnTime = 20000;
            final State state = DungeonState.getDungeonState();
            new TimedTaskRepeat(1000) {
                private int counter = 0;

                @Override
                public boolean checkCondition() {
                    return !(counter < respawnTime);
                }

                @Override
                public void performAction() {
                    if(GameView.isActive()){
                        counter += 1000;
                    }
                }

                @Override
                public void end() {
                    if(GameView.getState() == state){
                        new GiantTroll(spawnX, spawnY, bossArea);
                    }
                }
            };
        }
    }

    /**
     * Loads the stats of the unit. (Hp, Damage, ...)
     */
    @Override
    public void loadStats() {
        setMaxHp(2000);
        setDamage(30);
    }

    /**
     * This method is triggered every time the unit gets attacked and its hp doesn't drop to 0 or less.
     *
     * @param attacker The attacker unit.
     */
    @Override
    public void reactToBeingAttacked(Unit attacker) {

    }

    /**
     * This method is triggered every time an ally unit signals getting attacked and its hp doesn't drop to 0 or less.
     *
     * @param ally     The ally being attacked.
     * @param attacker The attacker unit.
     */
    @Override
    public void reactToAllyBeingAttacked(Unit ally, Unit attacker) {

    }

    /**
     * This method checks whether the unit passed as a parameter is in the attack range of the unit.
     *
     * @param unit The unit to check for.
     * @return True if the unit if in attack range. False if not.
     */
    @Override
    public boolean isInAttackRange(Unit unit) {
        if(unit == null) return false;
        return Utils.areSideBySideHeightOfCollisionBox(this, unit);
    }

    /**
     * Initializes the animations to be ready to used.
     */
    @Override
    public void loadAnimations() {
        setMoveLeftAnimation(new PerpetualAnimation(Utils.scaleAnimation(AnimationLoader.trollMoveLeft, sizeScale), (long) (400 / getSpeed()), -1, 42 * GameView.density() * sizeScale, 16 * GameView.density() * sizeScale, this));
        setMoveRightAnimation(new PerpetualAnimation(Utils.scaleAnimation(AnimationLoader.trollMoveRight, sizeScale), (long) (400 / getSpeed()), -1, 46 * GameView.density() * sizeScale, 16 * GameView.density() * sizeScale, this));
        setAttackRightAnimation(new AttackAnimation(Utils.scaleAnimation(AnimationLoader.trollAttackRight, sizeScale), 120, 1, 28 * GameView.density() * sizeScale, 20 * GameView.density() * sizeScale, this, getMoveRightAnimation()));
        setAttackLeftAnimation(new AttackAnimation(Utils.scaleAnimation(AnimationLoader.trollAttackLeft, sizeScale), 120, 1, 30 * GameView.density() * sizeScale, 20 * GameView.density() * sizeScale, this, getMoveLeftAnimation()));
        stompLeftCastAnimation = new CastAnimation(Utils.scaleAnimation(AnimationLoader.trollAttackLeft, sizeScale), 400, 1, 30 * GameView.density() * sizeScale, 20 * GameView.density() * sizeScale, this, getMoveLeftAnimation()) {
            @Override
            public void castAtStart() {
                stompAnimationStage = 1;
            }

            @Override
            public void castHalfWay() {

            }

            @Override
            public void castOnEnd() {
                stompX = getSolidBox().centerX();
                stompY = getSolidBox().centerY();
                stompAnimation.reset();
                stompAnimationStage = 2;
            }
        };
        stompRightCastAnimation = new CastAnimation(Utils.scaleAnimation(AnimationLoader.trollAttackRight, sizeScale), 400, 1, 28 * GameView.density() * sizeScale, 20 * GameView.density() * sizeScale, this, getMoveRightAnimation()) {
            @Override
            public void castAtStart() {
                stompAnimationStage = 1;
            }

            @Override
            public void castHalfWay() {

            }

            @Override
            public void castOnEnd() {
                stompX = getSolidBox().centerX();
                stompY = getSolidBox().centerY();
                stompAnimation.reset();
                stompAnimationStage = 2;
            }
        };
    }

    /**
     * Has the unit move in range of its target. Is called automatically on every frame if the unit is on attacking state. Must be overrode for different approaches.
     * Current approach is getting in melee range. (Side by side)
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

    public float getAxeDamage() {
        return axeDamage;
    }

    public float getRockDamage(){
        return rockDamage;
    }
}
