package com.tinslam.battleheart.quests;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.items.Item;
import com.tinslam.battleheart.items.armors.SimpleShield;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

public class RepelTheInvaders extends Quest{
    public static RepelTheInvaders repelTheInvaders;
    private int gold = 0;
    private int experience = 0;
    private static int number = 3;
    private int campsKilled = 0;
    private ArrayList<Item> items = new ArrayList<>();

    public RepelTheInvaders(int id, ArrayList<Integer> conditions) {
        super(id, conditions);

        repelTheInvaders = this;

        name = GameView.string(R.string.repel_the_invaders);
        description = GameView.string(R.string.repel_the_invaders_description);
//        rewards.add(experience + " " + GameView.string(R.string.experience));
//        rewards.add(gold + " " + GameView.string(R.string.gold));
        rewards.add(GameView.string(R.string.simple_shield));

        progress = GameView.string(R.string.enemy_camps_defeated) + " : " + campsKilled + " / " + number;
        items.add(new SimpleShield(""));
    }

    public static void campKilled(){
        if(repelTheInvaders == null) return;
        if(!repelTheInvaders.isActive()) return;
        repelTheInvaders.campsKilled = Utils.min(repelTheInvaders.campsKilled + 1, number);
        repelTheInvaders.updateProgress();
        repelTheInvaders.checkConditions();
    }

    @Override
    public void updateProgress() {
        progress = GameView.string(R.string.enemy_camps_defeated) + " : " + campsKilled + " / " + number;
        if(DungeonState.getDungeonState().getCurrentQuest() == this) DungeonState.getDungeonState().updateQuestProgressTextRenderer(progress);
    }

    @Override
    public void receiveRewards() {
        DungeonState.getDungeonState().addExpGained(experience);
        DungeonState.getDungeonState().addGoldGained(gold);
        PlayerStats.items.addAll(items);
    }

    @Override
    protected boolean checkWinConditions() {
        return campsKilled >= number;
    }

    @Override
    protected boolean checkFailConditions() {
        return false;
    }

    @Override
    public void questFinished() {

    }

    @Override
    public void questFailed() {

    }

    @Override
    protected void destroyQuest() {

    }
}
