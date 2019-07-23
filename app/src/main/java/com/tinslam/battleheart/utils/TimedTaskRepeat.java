package com.tinslam.battleheart.utils;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.interfaces.ActionInterface;
import com.tinslam.battleheart.interfaces.ConditionInterface;
import com.tinslam.battleheart.interfaces.EndInterface;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.constants.Consts;

import java.util.ArrayList;

public abstract class TimedTaskRepeat implements ActionInterface, ConditionInterface, EndInterface {
    private static final Object lock = new Object();
    private static ArrayList<TimedTaskRepeat> timers = new ArrayList<>();

    protected float time = 0;
    private float timePassed = 0;

    public TimedTaskRepeat(float time){
        this.time = time;

        addTimer(this);
    }

    private void tickTimer(){
        timePassed += Utils.frameInMilliSeconds() / (State.slowMo ? Consts.SLOW_MO_SKIP_FRAMES : 1);

        if(timePassed >= time){
            timePassed = -Float.MAX_VALUE;
            performAction();
            if(checkCondition()){
                removeTimer(this);
                end();
            }else{
                timePassed = 0;
            }
        }
    }

    public static void tick(){
        for(TimedTaskRepeat timedTask : timers){
            timedTask.tickTimer();
        }
    }

    private static void addTimer(final TimedTaskRepeat timedTask){
        new Event() {
            @Override
            public void performAction() {
                    timers.add(timedTask);
            }
        };
    }

    private static void removeTimer(final TimedTaskRepeat timedTask){
        new Event() {
            @Override
            public void performAction() {
                timers.remove(timedTask);
            }
        };
    }
}
