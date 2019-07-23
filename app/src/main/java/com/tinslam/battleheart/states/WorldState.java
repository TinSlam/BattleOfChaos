package com.tinslam.battleheart.states;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.UI.buttons.roundbuttons.RoundButton;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.GameActivity;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.utils.constants.Consts;

public class WorldState extends State{
    @Override
    public void handleBackPressed(){
        ActivityManager.switchToActivity(OpenGL2dActivity.openGL2dActivity, OpenGLActivity.class);
    }

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    @Override
    public void startState() {
        int buttonWidth = GameView.getScreenHeight() / 10;
        int buttonHeight = buttonWidth;
        Texture image = new Texture(TextureData.color_blue, buttonWidth, buttonHeight);
        new RoundButton(GameView.getScreenWidth() / 10,
                GameView.getScreenHeight() / 5,
                image,
                image,
                false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new PortalState(Consts.GREEN_VILLAGE), "");
                return true;
            }
        };
        new RoundButton(GameView.getScreenWidth() * 2 / 10,
                GameView.getScreenHeight() * 4 / 5,
                image,
                image,
                false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new PortalState(Consts.PASSAGE_TO_THE_FOREST), "");
                return true;
            }
        };
        new RoundButton(GameView.getScreenWidth() * 3 / 10,
                GameView.getScreenHeight() * 3 / 5,
                image,
                image,
                false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new PortalState(Consts.FOREST_OF_SHADOWS), "");
                return true;
            }
        };
        new RoundButton(GameView.getScreenWidth() * 4 / 10,
                GameView.getScreenHeight() / 5,
                image,
                image,
                false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new PortalState(Consts.ICE), "");
                return true;
            }
        };
        new RoundButton(GameView.getScreenWidth() * 5 / 10,
                GameView.getScreenHeight() * 3 / 5,
                image,
                image,
                false) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new PortalState(Consts.SNOW), "");
                return true;
            }
        };
    }

    @Override
    public void tick() {

    }

    @Override
    public void render() {

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
