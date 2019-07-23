package com.tinslam.battleheart.states;

import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;

public class PortalState extends State{
    private String stage;
    private TextRenderer stageNameRenderer;
    private int backgroundImage;

    PortalState(String stage){
        this.stage = stage;
    }

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void handleBackPressed() {
        if(GameView.stateChangeOnCD) return;
        GameView.setState(new WorldState(), "");
    }

    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    private void initBackground(){
        switch(stage){
            case Consts.GREEN_VILLAGE :
                backgroundImage = TextureData.backgroundPortalGreen;
                break;

            case Consts.PASSAGE_TO_THE_FOREST :
                backgroundImage = TextureData.backgroundPortalForest;
                break;

            case Consts.FOREST_OF_SHADOWS :
                backgroundImage = TextureData.backgroundPortalDarkForest;
                break;

            case Consts.ICE :
                backgroundImage = TextureData.backgroundPortalIce;
                break;

            case Consts.SNOW :
                backgroundImage = TextureData.backgroundPortalSnow;
                break;

            default :
                backgroundImage = TextureData.backgroundPortalGreen;
        }
    }

    @Override
    public void startState() {
        initBackground();
        stageNameRenderer = new TextRenderer(stage, GameView.getScreenWidth() / 2,
                GameView.getScreenHeight() / 5,
                GameView.getScreenWidth() / 2,
                GameView.getScreenHeight() / 7,
                Paint.Align.CENTER,
                false, false);
        stageNameRenderer.show();

        int buttonHeight = GameView.getScreenHeight() / 8;
        int buttonWidth = buttonHeight;
        int buttonsPerLine = 5;
        int xGap = (GameView.getScreenWidth() - buttonWidth * buttonsPerLine) / (buttonsPerLine + 1);
        int buttonsPerColumn = 3;
        int yGap = (int) ((GameView.getScreenHeight() * 2 / 3 - 64 * GameView.density() - buttonHeight * buttonsPerColumn) / (buttonsPerColumn - 1));

        for(int i = 0; i < buttonsPerColumn * buttonsPerLine; i++){
            if(Utils.isStageLocked(stage, (i + 1))){
                Texture image = new Texture(TextureData.level_empty_locked, buttonWidth, buttonHeight);
                final int finalI = i;
                RectangleButton button = new RectangleButton(xGap + (finalI % buttonsPerLine) * (buttonWidth + xGap),
                        (int) (GameView.getScreenHeight() / 3 + 32 * GameView.density() + (finalI / buttonsPerLine) * (buttonHeight + yGap)),
                        image, image, finalI != (buttonsPerColumn * buttonsPerLine - 1) ? (finalI + 1) + "" : GameView.string(R.string.boss), false) {
                    @Override
                    public boolean onDown() {
                        return true;
                    }

                    @Override
                    public boolean onUp() {
                        if(GameView.stateChangeOnCD) return true;
                        GameView.setState(new ArenaState(finalI + 1, stage), "");
                        return true;
                    }
                };
                button.setActive(false);
                button.getTextRenderer().show();
            }else{
                Texture image = new Texture(TextureData.level_empty, buttonWidth, buttonHeight);
                final int finalI1 = i;
                RectangleButton button = new RectangleButton(xGap + (finalI1 % buttonsPerLine) * (buttonWidth + xGap),
                        (int) (GameView.getScreenHeight() / 3 + 32 * GameView.density() + (finalI1 / buttonsPerLine) * (buttonHeight + yGap)),
                        image, image, finalI1 != (buttonsPerColumn * buttonsPerLine - 1) ? (finalI1 + 1) + "" : GameView.string(R.string.boss), false) {
                    @Override
                    public boolean onDown() {
                        return true;
                    }

                    @Override
                    public boolean onUp() {
                        if(GameView.stateChangeOnCD) return true;
                        GameView.setState(new ArenaState(finalI1 + 1, stage), "");
                        return true;
                    }
                };
                button.setActive(true);
                button.getTextRenderer().show();
            }
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), backgroundImage, 255);
    }

    @Override
    public void renderOver() {

    }

    @Override
    public boolean onActionDown(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onActionPointerDown(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onActionMove(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onActionUp(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onActionPointerUp(MotionEvent event) {
        return false;
    }

    @Override
    public void endState() {

    }
}
