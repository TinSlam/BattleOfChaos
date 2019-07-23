package com.tinslam.battleheart.quests;

import com.tinslam.battleheart.gameUtility.Event;

import java.util.ArrayList;

public abstract class Quest{
    private static ArrayList<Quest> quests = new ArrayList<>();
    private static final Object questsLock = new Object();
    private static ArrayList<Integer> questsDone = new ArrayList<>();

    protected int id;
    protected ArrayList<Integer> requirements = new ArrayList<>();
    protected String name;
    protected String description;
    protected String progress;
    protected ArrayList<String> rewards = new ArrayList<>();
    private boolean over = false;
    private boolean active = false;

    public Quest(int id, ArrayList<Integer> requirements){
        this.id = id;
        this.requirements = requirements;
        addQuest(this);
    }

    public void checkConditions(){
        if(over || !active) return;
        if(checkWinConditions()){
            over = true;
            questFinished();
            destroy();
//            active = false;
        }else if(checkFailConditions()){
            over = true;
            questFailed();
            destroy();
//            active = false;
        }
    }

    public static void clear(){
        for(Quest x : quests){
            x.destroy();
        }
    }

    public void destroy(){
        destroyQuest();
        removeQuest(this);
    }

    public abstract void updateProgress();

    public abstract void receiveRewards();

    protected abstract boolean checkWinConditions();

    protected abstract boolean checkFailConditions();

    public abstract void questFinished();

    public abstract void questFailed();

    protected abstract void destroyQuest();

    private static void addQuest(final Quest quest){
        new Event() {
            @Override
            public void performAction() {
                    quests.add(quest);
            }
        };
    }

    private static void removeQuest(final Quest quest){
        new Event() {
            @Override
            public void performAction() {
                    quests.remove(quest);
            }
        };
    }

    public static void questDone(final int id){
        new Event() {
            @Override
            public void performAction() {
                    questsDone.add(id);
            }
        };
    }

    public static void questDone(Quest quest){
        questDone(quest.id);
    }

    public boolean isUnlocked(){
        for(int i = 0; i < requirements.size(); i++){
            if(!questsDone.contains(requirements.get(i))) return false;
        }

        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getRewards() {
        return rewards;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isOver() {
        return over;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
}
