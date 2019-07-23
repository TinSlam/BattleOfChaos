package com.tinslam.battleheart.quests;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.collectables.Herb;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.MummyArcher;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.Troll;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.items.Item;
import com.tinslam.battleheart.items.armors.SimpleShield;
import com.tinslam.battleheart.states.DungeonState;

import java.util.ArrayList;

public class FindTheChief extends CollectQuest{
    private int gold = 0;
    private int experience = 0;
    private static int number = 1;
    private ArrayList<Item> items = new ArrayList<>();

    public FindTheChief(int id, ArrayList<Integer> conditions) {
        super(Herb.class, number, id, conditions);

        name = GameView.string(R.string.find_the_chief);
        description = GameView.string(R.string.find_the_chief_description);
//        rewards.add(experience + " " + GameView.string(R.string.experience));
//        rewards.add(gold + " " + GameView.string(R.string.gold));
//        rewards.add(GameView.string(R.string.simple_shield));

        progress = "";
//        items.add(new SimpleShield(""));
    }

    @Override
    public void updateProgress() {

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
}
