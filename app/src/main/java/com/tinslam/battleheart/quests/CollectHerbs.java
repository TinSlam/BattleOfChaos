package com.tinslam.battleheart.quests;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.collectables.Herb;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.items.Item;
import com.tinslam.battleheart.states.DungeonState;

import java.util.ArrayList;

public class CollectHerbs extends CollectQuest{
    private int gold = 0;
    private int experience = 0;
    private static int number = 11;
    private ArrayList<Item> items = new ArrayList<>();

    public CollectHerbs(int id, ArrayList<Integer> conditions) {
        super(Herb.class, number, id, conditions);

        name = GameView.string(R.string.collect_herbs);
        description = GameView.string(R.string.collect_herbs_description);
//        rewards.add(experience + " " + GameView.string(R.string.experience));
//        rewards.add(gold + " " + GameView.string(R.string.gold));
//        rewards.add(GameView.string(R.string.simple_shield));

        progress = GameView.string(R.string.herbs_collected) + " : " + counter + " / " + number;
//        items.add(new SimpleShield(""));
    }

    @Override
    public void updateProgress() {
        progress = GameView.string(R.string.herbs_collected) + " : " + counter + " / " + number;
        if(DungeonState.getDungeonState().getCurrentQuest() == this) DungeonState.getDungeonState().updateQuestProgressTextRenderer(progress);
    }

    @Override
    public void receiveRewards() {
        DungeonState.getDungeonState().addExpGained(experience);
        DungeonState.getDungeonState().addGoldGained(gold);
        PlayerStats.items.addAll(items);
    }

    @Override
    public void questFinished() {

    }

    @Override
    public void questFailed() {

    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        for(Herb herb : Herb.getHerbs()){
            herb.setCanBeCollected(active);
        }
    }
}
