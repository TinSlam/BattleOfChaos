package com.tinslam.battleheart.states;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;

import java.util.ArrayList;

public class LoadingState extends State{
    private static ArrayList<int[]> points = new ArrayList<>();
    private int counter = 0, frameCounter = 0;
    private boolean up = true;
    private State state;
    private TextRenderer text;

    static{
        for(int i = 0; i < 12; i++){
            points.add(new int[] {GameView.getScreenWidth() * i / 13, GameView.getScreenHeight() * 2 / 3});
        }
    }

    public LoadingState(State state){
        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.solid_black, 255);
        this.state = state;
        GameView.loading(this);
        text = new TextRenderer("Did you know this is a loading screen ?", GameView.getScreenWidth() / 2, GameView.getScreenHeight() * 4 / 5,
                GameView.getScreenWidth() - 128 * GameView.density(),
                GameView.getScreenHeight() / 20,
                Paint.Align.CENTER,
                true,
                false);
        text.setTextColor(Color.WHITE);
        text.show();
    }

    public void finishLoading(){
        GameView.loadingFinished();
        state.loadingEnded();
        text.destroy();
    }

    /**
     * This method is called when the game view loses focus.
     */
    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void handleBackPressed(){

    }

    /**
     * Listener for handling the key events.
     *
     * @param event The key event.
     */
    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    /**
     * Initializes the state. Is called when switching to a new state.
     */
    @Override
    public void startState() {

    }

    /**
     * Ticks the state.
     */
    @Override
    public void tick() {
        if(frameCounter == 0){
            if(up){
                if(counter == 12){
                    up = false;
                    counter = 11;
                }else{
                    counter++;
                }
            }else{
                if(counter == 0){
                    up = true;
                    counter = 1;
                }else{
                    counter--;
                }
            }
            frameCounter = 0;
        }else{
            frameCounter++;
        }
    }

    /**
     * Renders the state.
     */
    @Override
    public void render() {
        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.solid_black, 255);
    }

    /**
     * Is drawn after everything.
     */
    @Override
    public void renderOver() {

    }

    /**
     * Handles touch input when action is pressing and is the first finger touching the screen.
     *
     */
    @Override
    public boolean onActionDown(MotionEvent event) {
        return false;
    }

    /**
     * Handles touch input when action is pressing and is not the first finger touching the screen.
     *
     */
    @Override
    public boolean onActionPointerDown(MotionEvent event) {
        return false;
    }

    /**
     * Handles touch input when action is moving finger on the screen.
     *
     */
    @Override
    public boolean onActionMove(MotionEvent event) {
        return false;
    }

    /**
     * Handles touch input when action is releasing and is the first finger touching the screen.
     *
     */
    @Override
    public boolean onActionUp(MotionEvent event) {
        return false;
    }

    /**
     * Handles touch input when action is releasing and is not the first finger touching the screen.
     *
     */
    @Override
    public boolean onActionPointerUp(MotionEvent event) {
        return false;
    }

    /**
     * Is called when state is to be changed.
     */
    @Override
    public void endState() {

    }
}
