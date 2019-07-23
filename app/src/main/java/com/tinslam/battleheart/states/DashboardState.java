package com.tinslam.battleheart.states;

import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.PlayerStats;

import java.util.Random;

public class DashboardState extends State{
    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void handleBackPressed() {

    }

    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    @Override
    public void startState() {
        new RectangleButton((int) (GameView.getScreenWidth() - 200 * GameView.density()), (int) (GameView.getScreenHeight() / 2 - 64 * GameView.density()), (int) (128 * GameView.density()), (int) (48 * GameView.density()), TextureData.button_empty, TextureData.button_empty_hover, GameView.string(R.string.kingdom), false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                ActivityManager.switchToActivity(OpenGL2dActivity.openGL2dActivity, OpenGLActivity.class);
                return true;
            }
        }.getTextRenderer().show();

        new RectangleButton((int) (GameView.getScreenWidth() - 200 * GameView.density()), GameView.getScreenHeight() / 2, (int) (128 * GameView.density()), (int) (48 * GameView.density()), TextureData.button_empty, TextureData.button_empty_hover, GameView.string(R.string.settings), false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new SettingsState(), "");
                return true;
            }
        }.getTextRenderer().show();

        new RectangleButton((int) (GameView.getScreenWidth() - 200 * GameView.density()), (int) (GameView.getScreenHeight() / 2 + 64 * GameView.density()), (int) (128 * GameView.density()), (int) (48 * GameView.density()), TextureData.button_empty, TextureData.button_empty_hover, GameView.string(R.string.exit), false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                ActivityManager.closeGame();
                return true;
            }
        }.getTextRenderer().show();

        new RectangleButton((int) (50 * GameView.density()), GameView.getScreenHeight() / 2, (int) (128 * GameView.density()), (int) (48 * GameView.density()), TextureData.button_empty, TextureData.button_empty_hover, GameView.string(R.string.replay), false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new ReplayState(), "");
                return true;
            }
        }.getTextRenderer().show();

        new RectangleButton((int) (50 * GameView.density()), (int) (GameView.getScreenHeight() / 2 + 64 * GameView.density()), (int) (128 * GameView.density()), (int) (48 * GameView.density()), TextureData.button_empty, TextureData.button_empty_hover, GameView.string(R.string.reset_progress), false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                GameView.getState().setConfirmation(true, this, null);
                return true;
            }

            public void performOnUp(){
                PlayerStats.resetAll();
            }
        }.getTextRenderer().show();
    }

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.backgroundArenaForest1, 255);
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
