package com.tinslam.battleheart.entities.units.NPCs.neutralNPCs;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.NullButton;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Animations.PerpetualAnimation;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.quests.Quest;
import com.tinslam.battleheart.states.DungeonState;

import java.util.ArrayList;

/**
 * The class that contains all the QuestNpcs.
 */
public class QuestNpc extends NeutralNpc{
    private static final Object questNpcsLock = new Object();
    private static ArrayList<QuestNpc> questNpcs = new ArrayList<>();

    private Quest quest;
    private float questImageX, questImageY;

    /**
     * Constructor.
     * @param x The x position of the QuestNpc.
     * @param y The y position of the QuestNpc.
     */
    public QuestNpc(float x, float y) {
        super(x, y);

        setImage(getMoveLeftAnimation());
        setShowHealthBar(false);
        updateCollisionBox();
        questImageX = x + getCollisionBox().width() / 2 - getCollisionBox().width() / 4;
        questImageY = y - getCollisionBox().width() / 2 - 8 * GameView.density();

        addQuestNpc(this);
    }

    public static Object getQuestNpcsLock() {
        return questNpcsLock;
    }

    public static ArrayList<QuestNpc> getQuestNpcs() {
        return questNpcs;
    }

    /**
     * The AI behaviour of the Npc. Is called on every frame.
     * Looks for targets if there is none. Attacks if it has a target.
     */
    @Override
    public void ai() {

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
        return false;
    }

    /**
     * Destroys the QuestNpc. Removes it from the QuestNpcs list.
     */
    @Override
    public void destroyNeutralNpc() {
        removeQuestNpc(this);
    }

    /**
     * Ticks the QuestNpc.
     */
    @Override
    public void tickNeutralNpc() {

    }

    /**
     * Renders the QuestNpc.
     * Draws the animation.
     */
    @Override
    public void renderNeutralNpc(float xOffset, float yOffset) {
        if(getAnimation() != null) getAnimation().render(getX() + xOffset, getY() + yOffset);
        if(quest.isUnlocked()){
            if(!quest.isOver() && !quest.isActive()) renderQuest(xOffset, yOffset);
            else if(quest.isActive()) renderQuestInProgress(xOffset, yOffset);
        }
    }

    private void renderQuest(float xOffset, float yOffset){
        MyGL2dRenderer.drawLabel(questImageX + xOffset, questImageY + yOffset, getCollisionBox().width() / 2, getCollisionBox().width() / 2, TextureData.gold, 255);
    }

    private void renderQuestInProgress(float xOffset, float yOffset){
        MyGL2dRenderer.drawLabel(questImageX + xOffset, questImageY + yOffset, getCollisionBox().width() / 2, getCollisionBox().width() / 2, TextureData.hourglass, 255);
    }

    /**
     * Updates the collision box according to the image. Must be changed accordingly when a new image is used.
     */
    @Override
    public void updateCollisionBox(){
        if(getCollisionBox() != null) getCollisionBox().set((int) getX(), (int) getY(), (int) (getX() + getAnimation().getWidth()), (int) (getY() + getAnimation().getHeight()));
    }

    /**
     * Initializes the animations.
     */
    @Override
    public void loadAnimations() {
        setMoveLeftAnimation(new PerpetualAnimation(AnimationLoader.villager1Left, (long) (100000 / getSpeed()), -1, 0, 0, this));
//        setMoveRightAnimation(new PerpetualAnimation(AnimationLoader.greenGoblinMoveRight, (long) (400 / getSpeed()), -1, 4 * GameView.density(), 0, this));
//        setAttackRightAnimation(new AttackAnimation(AnimationLoader.greenGoblinAttackRight, 120, 1, 28 * GameView.density(), 20 * GameView.density(), this, getMoveRightAnimation()));
//        setAttackLeftAnimation(new AttackAnimation(AnimationLoader.greenGoblinAttackLeft, 120, 1, 30 * GameView.density(), 20 * GameView.density(), this, getMoveLeftAnimation()));
    }

    /**
     * Follows the target until gets in melee range.
     */
    @Override
    public boolean initTargetLocation() {
        return false;
    }

    /**
     * Adds the QuestNpc to the QuestNpcs list.
     */
    private static void addQuestNpc(final QuestNpc gg){
        new Event() {
            @Override
            public void performAction() {
                    questNpcs.add(gg);
            }
        };
    }

    /**
     * Removes the QuestNpc from the QuestNpcs list.
     */
    private static void removeQuestNpc(final QuestNpc gg){
        new Event() {
            @Override
            public void performAction() {
                    questNpcs.remove(gg);
            }
        };
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public void interact(){
        if(!quest.isUnlocked()) return;
        if(!quest.isOver() && !quest.isActive()){
            GameView.getState().setConfirmation(true, new NullButton() {
                public void performOnUp(){
                    DungeonState.getDungeonState().addQuest(quest);
                }
            }, GameView.string(R.string.accept_quest));
        }else if(quest.isActive() && quest.isOver()){
            Quest.questDone(quest);
            DungeonState.getDungeonState().removeQuest(quest);
            DungeonState.getDungeonState().receiveRewards(quest);
        }
    }
}