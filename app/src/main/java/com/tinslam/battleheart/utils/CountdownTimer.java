package com.tinslam.battleheart.utils;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.interfaces.ActionInterface;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.constants.Consts;

public abstract class CountdownTimer implements ActionInterface {
    private int time, counter = 0;
    private boolean pause = false, stop = false, stopAndPerform = false;

    public CountdownTimer(int time){
        this.time = time;
    }

    public void start(){
        new TimedTaskRepeat(10) {
            private boolean flag = false;

            @Override
            public boolean checkCondition() {
                return flag;
            }

            @Override
            public void performAction() {
                if(pause) return;
                if(stop) return;
                if(stopAndPerform){
                    flag = true;
                    return;
                }
                if(GameView.isActive()){
                    if(State.slowMo){
                        counter += 10 / Consts.SLOW_MO_SKIP_FRAMES;
                    }else{
                        counter += 10;
                    }
                }
                if(counter >= time) flag = true;
            }

            @Override
            public void end() {
                performAction();
            }
        };
    }

    public float getTime(){
        return Utils.convertMsToSeconds(counter);
    }

    public String getTimeAsString(){
        return Utils.formatStringTwoRadixPoint(getTime());
    }

    public void pause(){
        pause = true;
    }

    public void resume(){
        pause = false;
    }

    public void stop(){
        stop = true;
    }

    public void stopAndPerform(){
        stop();
        performAction();
    }
}