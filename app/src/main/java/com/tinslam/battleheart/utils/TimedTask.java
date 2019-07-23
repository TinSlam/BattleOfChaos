package com.tinslam.battleheart.utils;

import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.interfaces.ActionInterface;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.constants.Consts;

import java.util.ArrayList;

public abstract class TimedTask implements ActionInterface{
    private static final Object lock = new Object();
    private static ArrayList<TimedTask> timers = new ArrayList<>();

    private float time = 0;
    private float timePassed = 0;

    public TimedTask(float time){
        this.time = time;

        addTimer(this);
    }

    private void tickTimer(){
        timePassed += Utils.frameInMilliSeconds() / (State.slowMo ? Consts.SLOW_MO_SKIP_FRAMES : 1);

        if(timePassed >= time){
            timePassed = Float.MIN_VALUE;
            removeTimer(this);
            performAction();
        }
    }

    public static void tick(){
        for(TimedTask timedTask : timers){
            timedTask.tickTimer();
        }
    }

    private static void addTimer(final TimedTask timedTask){
        new Event() {
            @Override
            public void performAction() {
                    timers.add(timedTask);
            }
        };
    }

    private static void removeTimer(final TimedTask timedTask){
        new Event() {
            @Override
            public void performAction() {
                    timers.remove(timedTask);
            }
        };
    }
}
