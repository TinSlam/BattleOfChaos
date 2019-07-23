package com.tinslam.battleheart.quests;

import com.tinslam.battleheart.gameUtility.Event;

import java.util.ArrayList;

public abstract class CollectQuest extends Quest{
    private static ArrayList<CollectQuest> collectQuests = new ArrayList<>();
    private static final Object findQuestsLock = new Object();
    private Class collectable;
    private int number;
    protected int counter = 0;

    public CollectQuest(Class collectable, int number, int id, ArrayList<Integer> conditions){
        super(id, conditions);
        this.collectable = collectable;
        this.number = number;

        addFindQuest(this);
    }

    public static void collectableCollected(Class collectableClass) {
        for(CollectQuest quest : collectQuests){
            if(!quest.isActive()) continue;
            if(quest.collectable == collectableClass){
                quest.counter++;
                quest.updateProgress();
                quest.checkConditions();
            }
        }
    }

    @Override
    protected boolean checkWinConditions() {
        return counter >= number;
    }

    @Override
    protected boolean checkFailConditions() {
        return false;
    }

    @Override
    protected void destroyQuest() {
        removeFindQuest(this);
    }

    private static void addFindQuest(final CollectQuest collectQuest){
        new Event() {
            @Override
            public void performAction() {
                collectQuests.add(collectQuest);
            }
        };
    }

    private static void removeFindQuest(final CollectQuest collectQuest){
        new Event() {
            @Override
            public void performAction() {
                    collectQuests.remove(collectQuest);
            }
        };
    }
}
