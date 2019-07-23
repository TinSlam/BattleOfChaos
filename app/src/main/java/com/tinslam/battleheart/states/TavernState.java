package com.tinslam.battleheart.states;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.GameActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.utils.constants.Consts;

import java.util.ArrayList;

public class TavernState extends State{
    private static final Object charactersPickedLock = new Object();
    private static ArrayList<String> charactersPicked = new ArrayList<>();

    public static ArrayList<String> getCharactersPicked() {
        return charactersPicked;
    }

    public static Object getCharactersPickedLock() {
        return charactersPickedLock;
    }

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void handleBackPressed() {
        ActivityManager.switchToActivity(OpenGL2dActivity.openGL2dActivity, OpenGLActivity.class);
    }

    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    @Override
    public void startState() {
        int width = (int) (GameView.getScreenWidth() - 256 * GameView.density());
        width /= 5;
        int height = (int) ((float) width / Consts.PORTRAIT_WIDTH * Consts.PORTRAIT_HEIGHT);
        int xOffset = (int) (128 * GameView.density());
        int yOffset = (int) (16 * GameView.density());
        int i = 0;
        for(final String character : PlayerStats.getUnlockedCharacters()){
            Texture image;
            if(i == 5){
                xOffset += width / 2;
                yOffset += height;
                i = 0;
            }
            image = PlayerStats.getUnitPortrait(character);
            new RectangleButton(xOffset + i * width, yOffset, width, height, image.getTexture(), image.getTexture(), false) {
                @Override
                public boolean onDown() {
                    return true;
                }

                @Override
                public boolean onUp() {
                    if(charactersPicked.contains(character)){
                        charactersPicked.remove(character);
                    }else{
                        if(charactersPicked.size() == 3){
                            charactersPicked.remove(0);
                        }
                        charactersPicked.add(character);
                    }
                    return true;
                }
            };
            i++;
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        renderSlots();
        int width = (int) (GameView.getScreenWidth() - 256 * GameView.density());
        width /= 5;
        int xOffset = (int) (128 * GameView.density() + width / 2);
        int height = (int) ((float) width / Consts.PORTRAIT_WIDTH * Consts.PORTRAIT_HEIGHT);
        int yOffset = (int) (GameView.getScreenHeight() - 32 * GameView.density() - height);
        Texture image;
        for(String character : charactersPicked){
            MyGL2dRenderer.drawLabel(xOffset + charactersPicked.indexOf(character) * width, yOffset,
                    width, height, PlayerStats.getUnitPortrait(character).getTexture(), 255);
        }
    }

    @Override
    public void renderOver() {

    }

    private void renderSlots(){
        int width = (int) (GameView.getScreenWidth() - 256 * GameView.density());
        width /= 5;
        int height = (int) ((float) width / Consts.PORTRAIT_WIDTH * Consts.PORTRAIT_HEIGHT);
        int xOffset = (int) (128 * GameView.density());
        int yOffset = (int) (16 * GameView.density());
        int i = 0;
        for(int j = 0; j < 9; j++){
            if(i == 5){
                xOffset += width / 2;
                yOffset += height;
                i = 0;
            }
            MyGL2dRenderer.drawLabel(xOffset + i * width, yOffset,
                    width, height, TextureData.outlined_black_square, 255);
            i++;
        }
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
