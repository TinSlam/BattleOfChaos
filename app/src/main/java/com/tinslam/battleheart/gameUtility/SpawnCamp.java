package com.tinslam.battleheart.gameUtility;

import android.graphics.Rect;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.quests.RepelTheInvaders;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class SpawnCamp{
    private static final Object campsLock = new Object();
    private static ArrayList<SpawnCamp> camps = new ArrayList<>();

    private Class<?>[] unitsClass;
    private int number;
    private float x, y;
    private float spawnRadius, guardRadius;
    private ArrayList<Unit> units = new ArrayList<>();
    private final Object lock = new Object();
    private boolean removeCamp = false;

    public SpawnCamp(Class[] unitsClass, int number, float x, float y, float spawnRadius, float guardRadius){
        this.unitsClass = unitsClass;
        this.number = number;
        this.x = x;
        this.y = y;
        this.spawnRadius = spawnRadius;
        this.guardRadius = guardRadius;

        addCamp(this);

        new TimedTaskRepeat(RepelTheInvaders.repelTheInvaders != null && RepelTheInvaders.repelTheInvaders.isActive() ? 500 : 5000) {
            @Override
            public boolean checkCondition() {
                return removeCamp;
            }

            @Override
            public void performAction() {
                for(final Unit x : units){
                    if(!x.doesExist()){
                        new Event() {
                            @Override
                            public void performAction() {
                                units.remove(x);
                                if(units.isEmpty()) campDied();
                            }
                        };
                    }
                }
            }

            @Override
            public void end() {

            }
        };
    }

    public static void spawnCamps(){
        for(SpawnCamp x : camps){
            x.spawnCamp();
        }
    }

    private void campDied(){
        RepelTheInvaders.campKilled();
        new TimedTask(5000) {
            @Override
            public void performAction() {
                if(removeCamp) return;
                spawnCamp();
            }
        };
    }

    private void spawnCamp(){
        if(units.isEmpty()){
            for(int i = 0; i < number; i++){
                try {
                    Class<?> unitClass = unitsClass[Utils.getRandomIntegerInTheRange(0, unitsClass.length - 1, null)];
                    Constructor constructor = unitClass.getConstructor(float.class, float.class);
                    Unit unit = (Unit) constructor.newInstance(0, 0);
                    unit.updateCollisionBox();
                    unit.updateSolidBox();
                    int x, y;
                    do{
                        x = Utils.getRandomIntegerInTheRange((int) (this.x - spawnRadius), (int) (this.x + spawnRadius), null);
                        y = Utils.getRandomIntegerInTheRange((int) (this.y - spawnRadius), (int) (this.y + spawnRadius), null);
                    }while(!DungeonState.getDungeonState().canMove(new Rect(x, y,
                            x + unit.getSolidBox().width(), y + unit.getSolidBox().height()), unit.getXClearance(), unit.getYClearance()));
                    unit.setPosition(x, y + unit.getSolidBox().height() - unit.getCollisionBox().height());
                    unit.setGuardPoint(unit.getX(), unit.getY(), guardRadius);
                    unit.setOnGuard(true);
                    unit.tick();
                    units.add(unit);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addCamp(final SpawnCamp camp){
        new Event() {
            @Override
            public void performAction() {
                    camps.add(camp);
            }
        };
    }

    private static void removeCamp(final SpawnCamp camp){
        camp.removeCamp = true;
        new Event() {
            @Override
            public void performAction() {
                    camps.remove(camp);
            }
        };
    }

    public static void clear(){
        for(SpawnCamp x : camps){
            removeCamp(x);
        }
    }
}