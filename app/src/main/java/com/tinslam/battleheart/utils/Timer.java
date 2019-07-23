package com.tinslam.battleheart.utils;

import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.constants.Consts;

import java.sql.SQLOutput;

public class Timer{
    private int counter = 0;
    private boolean pause = false, stop = false;
    private long time;

    public void start(){
        time = System.nanoTime();
        new TimedTaskRepeat(1) {
            private boolean flag = false;

            @Override
            public boolean checkCondition() {
                return flag;
            }

            @Override
            public void performAction() {
                if(pause) return;
                if(stop){
                    flag = true;
                    return;
                }
                if(GameView.isActive()){
                    counter += (System.nanoTime() - Timer.this.time) / 1000000;
                    Timer.this.time = System.nanoTime();
                }
            }

            @Override
            public void end() {

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
}