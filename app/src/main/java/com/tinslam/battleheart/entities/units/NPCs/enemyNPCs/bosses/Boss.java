package com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses;

import android.graphics.Rect;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.NullButton;
import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.EnemyNpc;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.states.ReplayState;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public abstract class Boss extends EnemyNpc {
    private static final Object bossesLock = new Object();
    private static ArrayList<Boss> bosses = new ArrayList<>();

    protected Rect bossArea;
    protected boolean triggered = false;
    protected float spawnX;
    protected float spawnY;
    private int triggerCounter = 0;
    private int triggerStage = 0;
    private int seed = (int) (Math.random() * Integer.MAX_VALUE);
    protected Random random;

    /**
     * Constructor.
     *
     * @param x The x position of the Npc.
     * @param y The y position of the Npc.
     */
    Boss(float x, float y, Rect bossArea) {
        super(x, y);

        this.spawnX = x;
        this.spawnY = y;

        this.bossArea = bossArea;
        constructor();
    }

    Boss(float x, float y, Rect bossArea, int seed) {
        super(x, y);

        this.spawnX = x;
        this.spawnY = y;
        this.seed = seed;

        this.bossArea = bossArea;
        constructor();
    }

    private void constructor(){
        random = new Random(seed);
        setShowHealthBar(false);
        getHealthBar().setForegroundTexture(TextureData.solid_red);

        reset();

        if(BattleState.getBattleState() instanceof ReplayState){
            triggered = true;
            triggerStage = 0;
            triggerCounter = 0;
        }

        addBoss(this);
    }

    public static Object getBossesLock() {
        return bossesLock;
    }

    public static ArrayList<Boss> getBosses() {
        return bosses;
    }

    public abstract void reset();

    public abstract void tickBoss();

    public abstract void renderBoss(float xOffset, float yOffset);

    public abstract void destroyBoss();

    public Rect getBossArea(){
        return bossArea;
    }

    @Override
    protected void tickEnemyNpc() {
        tickBoss();
        if(!triggered){
            if(BattleState.getBattleState().getSelectedPc() == null) return;
            if(bossArea == null) return;
            if(triggerStage == 0){
                if(bossArea.contains(BattleState.getBattleState().getSelectedPc().getCollisionBox()) &&
                        Utils.distance(BattleState.getBattleState().getSelectedPc(), bossArea.centerX(), bossArea.centerY()) < GameView.getScreenHeight() / 2){
                    triggerStage = 1;
                    final Boss self = this;
                    GameView.getState().setConfirmation(true, new NullButton() {
                        public void performOnUp(){
                            if(BattleState.getBattleState() instanceof DungeonState){
                                DungeonState.getDungeonState().triggerBoss(self);
                            }else if(BattleState.getBattleState() instanceof ReplayState){
                                ReplayState.getReplayState().triggerBoss(self);
                            }
                            triggerStage = 3;
                            triggerCounter = 0;
                        }

                        public void performOnNo(){
                            triggerStage = 2;
                            triggerCounter = 0;
                        }
                    }, GameView.string(R.string.do_you_want_to_challenge_the_boss));
                }
            }else if(triggerStage == 2){
                triggerCounter++;
                if(triggerCounter >= GameThread.maxFps * 4) triggerStage = 0;
            }else if(triggerStage == 3){
                if(DungeonState.getDungeonState().cutSceneEnded()){
                    DungeonState.getDungeonState().startReplay(this);
                    triggerStage = 0;
                    triggered = true;
                }
            }
        }
    }

    @Override
    protected void renderEnemyNpc(float xOffset, float yOffset) {
        renderBoss(xOffset, yOffset);
        if(triggered){
            getHealthBar().render(new Rect(GameView.getScreenWidth() / 2 + GameView.getScreenWidth() / 20,
                    GameView.getScreenHeight() / 10, GameView.getScreenWidth() - GameView.getScreenWidth() / 20,
                    GameView.getScreenHeight() / 10 + GameView.getScreenHeight() / 20 * 2 / 3));
        }
    }

    @Override
    protected void destroyEnemyNpc() {
        removeBoss(this);
        destroyBoss();
        if(!BattleState.getBattleState().isOver()){
            if(BattleState.getBattleState() instanceof DungeonState){
                DungeonState.getDungeonState().bossDone();
            }else{
                ReplayState.getReplayState().bossDone();
            }
        }
    }

    public boolean isTriggered() {
        return triggered;
    }

    private static void addBoss(final Boss boss){
        new Event() {
            @Override
            public void performAction() {
                    bosses.add(boss);
            }
        };
    }

    private static void removeBoss(final Boss boss){
        new Event() {
            @Override
            public void performAction() {
                    bosses.remove(boss);
            }
        };
    }

    public int getSeed(){
        return seed;
    }
}
