package com.tinslam.battleheart.gameUtility;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.GreenGoblin;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.MummyArcher;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.Troll;
import com.tinslam.battleheart.states.ArenaState;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.FileManager;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.NameConsts;

import java.util.ArrayList;

/**
 * A class that handles what needs to happen in a level.
 */
public class Level{
    private ArrayList<ArrayList<String>> waves;
    private int index = 0;
    private int mapTop;
    private static Level level;
    private ArenaState arenaState;

    /**
     * Constructor.
     * @param path The path to the file to read from.
     * @param mapTop The top of the walkable ground.
     */
    public Level(ArenaState arenaState, String path, int mapTop){
        Level.level = this;
        this.arenaState = arenaState;

        this.mapTop = mapTop;
        waves = FileManager.loadSpawns(path);
        spawnWave();
    }

    /**
     * Automatically spawns the next wave if the current wave takes too long.
     */
    private void autoWaveSpawn(final int wave){
        if(arenaState != BattleState.getBattleState()) return;
        // If this object is not the level playing obsolete it.
        if(this != level) return;
        // Set a timer to spawn.
        new TimedTaskRepeat(1000) {
            private int counter = 0;

            @Override
            public boolean checkCondition() {
                return counter == Consts.TIME_AUTO_WAVE_SPAWN;
            }

            @Override
            public void performAction() {
                if(GameView.isActive()){
                    counter += 1000;
                }
            }

            @Override
            public void end() {
                if(getIndex() == wave){
                    if(wave != waves.size()){
                        spawnWave();
                    }
                }
            }
        };
    }

    /**
     * Spawns a new wave.
     */
    public synchronized void spawnWave(){
        if(arenaState != BattleState.getBattleState()) return;
        // If this object is not the level playing obsolete it.
        if(this != level) return;
        // If no waves left.
        if(isLastWave()){
            BattleState.getBattleState().win();
            return;
        }

        new TimedTask(Consts.TIME_WAVE_SPAWN_DELAY) {
            @Override
            public void performAction() {
                if(arenaState != BattleState.getBattleState()) return;

                // Start the spawn.
                try{
                    for(String npc : waves.get(index)){
                        float x, y = (float) ((Math.random() * (GameView.getScreenHeight() - mapTop - 60 * GameView.density())) + mapTop + 60 * GameView.density());
                        if(Math.random() < 0.5){
                            x = -100 * GameView.density();
                        }else{
                            x = 100 * GameView.density() + GameView.getScreenWidth();
                        }
                        switch(npc){
                            case NameConsts.TROLL :
                                new Troll(x, y);
                                break;

                            case NameConsts.GREEN_GOBLIN :
                                new GreenGoblin(x, y);
                                break;

                            case NameConsts.MUMMY_ARCHER :
                                new MummyArcher(x, y);
                                break;
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                setIndex(getIndex() + 1);
                autoWaveSpawn(getIndex());
            }
        };
    }

    /**
     * @return Whether the current wave is the last wave or not.
     */
    private boolean isLastWave(){
        return getIndex() == waves.size();
    }

    /**
     *
     * @return Wave index.
     */
    private int getIndex(){
        return index;
    }

    /**
     * Sets the index.
     */
    private void setIndex(int index){
        this.index = index;
    }

    /**
     *
     * @return The most recently created object of the Level class.
     */
    public static Level getLevel(){
        return level;
    }
}
