package com.tinslam.battleheart.states;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.KeyEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.PCs.Archer;
import com.tinslam.battleheart.entities.units.PCs.Knight;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.PCs.Priest;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.Level;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.NameConsts;

import java.util.HashMap;

/**
 * The class that handles the arena fighting state.
 */
public class ArenaState extends BattleState{
    private int backgroundImage;
    private String level, stage;
    private int lvl;
    private TextRenderer victoryText;
    private HashMap<Pc, Integer> pcLabelHashMap = new HashMap<>();
    private HashMap<Pc, Integer> pcLabelHashMap2 = new HashMap<>();
    private HashMap<Pc, Integer> pcLabelHashMap3 = new HashMap<>();

    /**
     * Constructor.
     */
    ArenaState(int lvl, String stage){
        super();

        this.stage = stage;
        this.lvl = lvl;
        this.level = stage + lvl + ".tls";;
    }

    /**
     * Is called when the game view loses focus.
     */
    @Override
    public void surfaceDestroyed() {

    }

    /**
     * Listener for handling the back key events.
     */
    @Override
    public void onBackPressed() {
        if(GameView.stateChangeOnCD) return;
        GameView.setState(new PortalState(stage), "");
    }

    @Override
    public void pcDied(Pc pc) {
        disablePcPortrait(pc);
    }

    /**
     * Listener for handling the key events.
     * @param event The key event.
     */
    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    /**
     * Initializes the state. Is called when switching to a new state.
     */
    @Override
    public void startBattleState() {
        goldTextRenderer.show();
        new Level(this, level, getMapTop());
        loadPcs();
        new Event() {
            @Override
            public void performAction() {
                new Event() {
                    @Override
                    public void performAction() {
                        initPcPortraits();
                    }
                };
            }
        };
    }

    private void loadPcs(){
        if(TavernState.getCharactersPicked().isEmpty()){
            for(int i = 0; i < Utils.min(PlayerStats.getUnlockedCharacters().size(), 4); i++){
                TavernState.getCharactersPicked().add(PlayerStats.getUnlockedCharacters().get(i));
            }
        }
        int i = 0;
        float x, y;
        for(String character : TavernState.getCharactersPicked()){
            switch(i){
                case 0 :
                    x = Utils.widthPercentage(50);
                    y = Utils.heightPercentage(40);
                    break;

                case 1 :
                    x = Utils.widthPercentage(50);
                    y = Utils.heightPercentage(80);
                    break;

                case 2 :
                    x = Utils.widthPercentage(20);
                    y = Utils.heightPercentage(60);
                    break;

                default :
                    x = Utils.widthPercentage(80);
                    y = Utils.heightPercentage(60);
                    break;
            }
            switch(character){
                case NameConsts.KNIGHT :
                    new Knight(x, y);
                    break;

                case NameConsts.PRIEST :
                    new Priest(x, y);
                    break;

                case NameConsts.ARCHER :
                    new Archer(x, y);
                    break;

                default :
                    System.out.println("Arena state loadPcs() method switch command default called for the character : " + character);
            }
            i++;
        }
        new Event() {
            @Override
            public void performAction() {
                new Event() {
                    @Override
                    public void performAction() {
                        for(int j = 0; j < Pc.getPcs().size(); j++){
                            pcLabelHashMap.put(Pc.getPcs().get(j), PlayerStats.getUnitPortrait(Pc.getPcs().get(j).getClass().getSimpleName()).getTexture());
                            pcLabelHashMap2.put(Pc.getPcs().get(j), TextureData.solid_green);
                            pcLabelHashMap3.put(Pc.getPcs().get(j), TextureData.solid_black);
                        }
                        loadSpells();
                    }
                };
            }
        };
    }

    /**
     * Is called when player defeats all waves.
     */
    @Override
    public void battleStateWin(){
        for(RectangleButton button : getSpellButtons()){
            button.getTextRenderer().hide();
        }
        for(Pc x : Pc.getPcs()){
            x.setTarget(null);
            x.commandMove(Float.MAX_VALUE, x.getY());
        }

        int totalExp = getExpGained();
        PlayerStats.gold += getGoldGained();
        PlayerStats.setLastLevelUnlocked(Utils.max(PlayerStats.getLastLevelUnlocked(), Utils.getLevelValue(stage, lvl) + 1));
        PlayerStats.updateAccountStats();
        new TimedTaskRepeat(4000 / totalExp) {
            @Override
            public boolean checkCondition() {
                return getExpGained() == 0;
            }

            @Override
            public void performAction() {
                for(int i = 0; i < Pc.getPcs().size(); i++){
                    if(!Pc.getPcs().get(i).doesExist()) continue;
                    String character = Pc.getPcs().get(i).getClass().getSimpleName();

                    if(getExpGained() > 0){
                        if(Utils.getExpNeeded(PlayerStats.getUnitLvl(character)) - PlayerStats.getUnitExp(character) == 1){
                            PlayerStats.lvlUpUnit(character);
                        }
                        PlayerStats.setUnitExp(character, PlayerStats.getUnitExp(character) + 1);
                        setExpGained(getExpGained() - 1);
                    }
                }
            }

            @Override
            public void end() {

            }
        };
        for(Pc x : Pc.getPcs()){
            PlayerStats.updateUnitStats(x.getClass().getSimpleName());
        }
        new TimedTask(3000) {
            @Override
            public void performAction() {
                if(GameView.stateChangeOnCD) return;
                GameView.setState(new PortalState(stage), "");
            }
        };

        goldTextRenderer.destroy();
        victoryText = new TextRenderer(GameView.string(R.string.victory) + " !",
                GameView.getScreenWidth() / 2, GameView.getScreenHeight() / 4,
                GameView.getScreenWidth() * 2 / 3,
                GameView.getScreenHeight() * 2 / 3,
                Paint.Align.CENTER, false, false);
        victoryText.show();
        victoryText.setTextColor(Color.GREEN);
        victoryText.setTextAlpha(0);
        new TimedTaskRepeat(10) {
            private boolean flag = true;

            @Override
            public boolean checkCondition() {
                return !(victoryText.getTextAlpha() != 255 || flag);
            }

            @Override
            public void end() {

            }

            @Override
            public void performAction() {
                if(victoryText.getTextAlpha() == 0) flag = false;
                if(!flag) victoryText.setTextAlpha(Utils.min(255, victoryText.getTextAlpha() + 2));
            }
        };
    }

    /**
     * Is called when player loses all units.
     */
    @Override
    public void battleStateLose(){
        if(GameView.stateChangeOnCD) return;
        GameView.setState(new ArenaState(lvl, stage), "");
    }

    /**
     * Initializes the background of the state.
     */
    @Override
    public void initBackground(){
        switch(stage){
            case Consts.GREEN_VILLAGE :
                backgroundImage = TextureData.backgroundArenaGrass;
                break;

            case Consts.PASSAGE_TO_THE_FOREST :
                backgroundImage = TextureData.backgroundArenaForest1;
                break;

            case Consts.FOREST_OF_SHADOWS :
                backgroundImage = TextureData.backgroundArenaForest2;
                break;

            case Consts.ICE :
                backgroundImage = TextureData.backgroundArenaIce;
                break;

            case Consts.SNOW :
                backgroundImage = TextureData.backgroundArenaSnow;
                break;

            default :
                backgroundImage = TextureData.backgroundArenaGrass;
        }
        getMapRect().set(0, getMapTop(), GameView.getScreenWidth(), GameView.getScreenHeight());
    }

    /**
     * Ticks the state.
     */
    @Override
    public void tickBattleState() {

    }

    /**
     * Renders the state.
     * Sorts the entities list to have them be drawn in order depending on how close they are.
     * Draws the background.
     */
    @Override
    public void renderBattleState() {

    }

    @Override
    public void renderOverBattleState(){
        if(isWin()) drawVictoryLayout();
    }

    /**
     * Checks whether an area on the map is possible to move to.
     */
    @Override
    public boolean canMove(Rect rect, int xClearance, int yClearance) {
        return true;
    }

    /**
     * Checks whether an area on the map is out of bounds.
     */
    @Override
    public boolean isOutOfBounds(int x, int y) {
        return !isWin() && !getMapRect().contains(x, y);
    }

    private void drawVictoryLayout(){
        int width = (int) (64 * GameView.density());
        int gap = (int) (32 * GameView.density());
        int xOffset = (GameView.getScreenWidth() - (((Pc.getPcs().size() - 1) * gap) + Pc.getPcs().size() * width)) / 2;
        int height = (int) (12 * GameView.density());
        int yOffset = (int) (GameView.getScreenHeight() * 3f / 4);

        for(int i = 0; i < Pc.getPcs().size(); i++){
            int texture = pcLabelHashMap3.get(Pc.getPcs().get(i));
            MyGL2dRenderer.drawLabel(xOffset + i * (width + gap) - 2 * GameView.density(),
                    yOffset - 2 * GameView.density(),
                    width + 4 * GameView.density(),
                    height + 4 * GameView.density(),
                    texture, 255);
        }
        int expWidth;
        Texture portrait;
        for(int i = 0; i < Pc.getPcs().size(); i++){
            Pc pc = Pc.getPcs().get(i);
            String character = Pc.getPcs().get(i).getClass().getSimpleName();

            expWidth = (int) ((float) (PlayerStats.getUnitExp(character) - Utils.getExpNeeded(PlayerStats.getUnitLvl(character) - 1)) /
                    (Utils.getExpNeeded(PlayerStats.getUnitLvl(character)) - Utils.getExpNeeded(PlayerStats.getUnitLvl(character) - 1)) * width);
            portrait = PlayerStats.getUnitPortrait(character);

            portrait = new Texture(portrait.getTexture(), width, width * portrait.getHeight() / portrait.getWidth());

            int texture = pcLabelHashMap.get(pc);
            MyGL2dRenderer.drawLabel(xOffset + i * (width + gap),
                    yOffset - 80 * GameView.density(),
                    width,
                    width * portrait.getHeight() / portrait.getWidth(),
                    texture, 255);

            texture = pcLabelHashMap2.get(pc);
            MyGL2dRenderer.drawLabel(xOffset + i * (width + gap),
                    yOffset,
                    expWidth,
                    height,
                    texture, 255);
        }
    }

    /**
     * Draws the background.
     */
    public void renderBackground(){
        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), backgroundImage, 255);
    }
}
