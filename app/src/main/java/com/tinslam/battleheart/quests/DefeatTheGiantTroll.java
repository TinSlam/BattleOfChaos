package com.tinslam.battleheart.quests;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.Troll;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses.GiantTroll;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.items.Item;
import com.tinslam.battleheart.items.armors.SimpleShield;
import com.tinslam.battleheart.states.DungeonState;

import java.util.ArrayList;

public class DefeatTheGiantTroll extends KillQuest{
    private int gold = 0;
    private int experience = 0;
    private static int number = 1;
    private ArrayList<Item> items = new ArrayList<>();

    public DefeatTheGiantTroll(int id, ArrayList<Integer> conditions) {
        super(GiantTroll.class, number, id, conditions);

        name = GameView.string(R.string.defeat_the_giant_troll);
        description = GameView.string(R.string.defeat_the_giant_troll_description);
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
