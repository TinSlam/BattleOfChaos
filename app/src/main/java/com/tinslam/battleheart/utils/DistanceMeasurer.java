package com.tinslam.battleheart.utils;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.Unit;

public class DistanceMeasurer{
    private Unit unit;
    private float lastX, lastY;
    private float distance = 0;
    private boolean start = false;

    public DistanceMeasurer(Unit unit){
        this.unit = unit;
    }

    public void start(){
        lastX = unit.getX();
        lastY = unit.getY();
        start = true;
    }

    public void mesaure(){
        if(start){
            float x2 = unit.getX(), y2 = unit.getY();
            if(x2 != lastX || y2 != lastY){
                distance += Utils.distance(x2, y2, lastX, lastY);
                lastX = x2;
                lastY = y2;
            }
        }
    }

    public float getDistance(){
        return distance / GameView.density();
    }

    public String getDistanceAsString(){
        return String.valueOf((int) getDistance());
    }
}
