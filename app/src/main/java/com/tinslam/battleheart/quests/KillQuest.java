package com.tinslam.battleheart.quests;

import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

public abstract class KillQuest extends Quest{
    private static ArrayList<KillQuest> killQuests = new ArrayList<>();
    private static final Object killQuestsLock = new Object();

    protected int killCounter = 0;
    private int killsNeeded;
    private Class preyClass;

    KillQuest(Class preyClass, int killsNeeded, int id, ArrayList<Integer> conditions){
        super(id, conditions);
        this.killsNeeded = killsNeeded;
        this.preyClass = preyClass;

        addKillQuest(this);
    }

    public static void unitDied(Class unitClass){
        for(KillQuest quest : killQuests){
            if(!quest.isActive()) continue;
            if(quest.preyClass == unitClass){
                quest.killCounter = Utils.min(quest.killCounter + 1, quest.killsNeeded);
                quest.updateProgress();
                quest.checkConditions();
            }
        }
    }

    @Override
    public boolean checkWinConditions(){
        return killCounter >= killsNeeded;
    }

    @Override
    public boolean checkFailConditions(){
        return false;
    }

    @Override
    protected void destroyQuest() {
        removeKillQuest(this);
    }

    private static void addKillQuest(final KillQuest killQuest){
        new Event() {
            @Override
            public void performAction() {
                killQuests.add(killQuest);
            }
        };
    }

    private static void removeKillQuest(final KillQuest killQuest){
        new Event() {
            @Override
            public void performAction() {
                    killQuests.remove(killQuest);
            }
        };
    }
}
