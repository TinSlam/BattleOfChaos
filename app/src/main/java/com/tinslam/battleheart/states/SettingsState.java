package com.tinslam.battleheart.states;

import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;

public class SettingsState extends State{
    private int sliderX;

    private static int staticOptionImageWidth = (int) (24 * GameView.density());
    private static int staticOptionImageHeight = (int) (24 * GameView.density());
    private static int staticSliderImageWidth = (int) (24 * GameView.density());
    private static int staticSliderImageHeight = (int) (32 * GameView.density());

    private boolean sliderGrabbed = false;

    /**
     * This method is called when the game view loses focus.
     */
    @Override
    public void surfaceDestroyed() {

    }

    /**
     * Listener for handling the key events.
     *
     * @param event The key event.
     */
    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    @Override
    public void handleBackPressed(){
        if(GameView.stateChangeOnCD) return;
        GameView.setState(new DashboardState(), "");
    }

    /**
     * Initializes the state. Is called when switching to a new state.
     */
    @Override
    public void startState() {
        byte option = BattleState.inputCommand;
        float x = GameView.getScreenWidth() / 3 + 64 * GameView.density() + option * GameView.getScreenWidth() * 4 / 9;
        float y = GameView.getScreenHeight() / 6 - 8 * GameView.density() + 16 * GameView.density() / 2;

        sliderX = (int) (x - staticSliderImageWidth / 2);

        TextRenderer inputCommandTextRenderer = new TextRenderer(GameView.string(R.string.command_input) + " :", GameView.getScreenWidth() / 6, GameView.getScreenHeight() / 6,
                GameView.getScreenWidth() / 3 - 64 * GameView.density(),
                GameView.getScreenHeight() / 8,
                Paint.Align.CENTER,
                false, false);

        TextRenderer inputCommandOption1 = new TextRenderer(GameView.string(R.string.command_input_drag) + " (Disabled)",
                GameView.getScreenWidth() / 3 + 64 * GameView.density(), y + 32 * GameView.density(),
                Float.MAX_VALUE,
                16 * GameView.density() / 3 * GameView.density(),
                Paint.Align.CENTER,
                false, false);

        TextRenderer inputCommandOption2 = new TextRenderer(GameView.string(R.string.command_input_point),
                GameView.getScreenWidth() / 3 + 64 * GameView.density() + GameView.getScreenWidth() * 4 / 9, y + 32 * GameView.density(),
                Float.MAX_VALUE,
                16 * GameView.density() / 3 * GameView.density(),
                Paint.Align.CENTER,
                false, false);

        inputCommandTextRenderer.show();
        inputCommandOption1.show();
        inputCommandOption2.show();
    }

    /**
     * Ticks the state.
     */
    @Override
    public void tick() {

    }

    /**
     * Renders the state.
     */
    @Override
    public void render() {
        try{
            float y = GameView.getScreenHeight() / 6 - 8 * GameView.density() + 16 * GameView.density() / 2;

            MyGL2dRenderer.drawLabel((int) (8 * GameView.density()), (int) (8 * GameView.density()), (int) (GameView.getScreenWidth() - 16 * GameView.density()), (int) (GameView.getScreenHeight() - 16 * GameView.density()), TextureData.light_brown_panel, 255);
            MyGL2dRenderer.drawLabel(GameView.getScreenWidth() / 3 + 64 * GameView.density(), GameView.getScreenHeight() / 6 - 8 * GameView.density(), GameView.getScreenWidth() * 4 / 9, 16 * GameView.density(), TextureData.brown_panel, 255);
            MyGL2dRenderer.drawLabel(GameView.getScreenWidth() / 3 + 64 * GameView.density() - staticOptionImageWidth / 2,
                    y - staticOptionImageHeight / 2, 24 * GameView.density(), 24 * GameView.density(), TextureData.color_yellow, 255);
            MyGL2dRenderer.drawLabel(GameView.getScreenWidth() / 3 + 64 * GameView.density() + GameView.getScreenWidth() * 4 / 9 - staticOptionImageWidth / 2,
                    y - staticOptionImageHeight / 2, 24 * GameView.density(), 24 * GameView.density(), TextureData.color_yellow, 255);
            MyGL2dRenderer.drawLabel(sliderX, y - staticSliderImageHeight / 2, 24 * GameView.density(), 32 * GameView.density(), TextureData.yellow_panel, 255);
        }catch(Exception ignored){}
    }

    /**
     * Is drawn after everything.
     */
    @Override
    public void renderOver() {

    }

    /**
     * Handles touch input when action is pressing and is the first finger touching the screen.
     */
    @Override
    public boolean onActionDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        float tempY = GameView.getScreenHeight() / 6 - 8 * GameView.density() + 16 * GameView.density() / 2;

        if(Utils.isInRect(x, y,
                GameView.getScreenWidth() / 3 + 64 * GameView.density() - 24 * GameView.density() / 2, tempY - staticSliderImageHeight / 2,
                GameView.getScreenWidth() / 3 + 64 * GameView.density() + GameView.getScreenWidth() * 4 / 9 + 24 * GameView.density() / 2, tempY - staticSliderImageHeight / 2 + 32 * GameView.density())){
            sliderGrabbed = true;
        }
        return true;
    }

    /**
     * Handles touch input when action is pressing and is not the first finger touching the screen.
     */
    @Override
    public boolean onActionPointerDown(MotionEvent event) {
        return true;
    }

    /**
     * Handles touch input when action is moving finger on the screen.
     */
    @Override
    public boolean onActionMove(MotionEvent event) {
        float x = event.getX();

        if(sliderGrabbed){
            float min = GameView.getScreenWidth() / 3 + 64 * GameView.density() - 24 * GameView.density() / 2;
            float max = GameView.getScreenWidth() / 3 + 64 * GameView.density() + GameView.getScreenWidth() * 4 / 9 - 24 * GameView.density() / 2;
            float pos = x - 24 * GameView.density() / 2;
            sliderX = (int) Utils.min(max, Utils.max(min, pos));
        }
        return true;
    }

    /**
     * Handles touch input when action is releasing and is the first finger touching the screen.
     */
    @Override
    public boolean onActionUp(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        sliderGrabbed = false;

        float tempY = GameView.getScreenHeight() / 6 - 8 * GameView.density() + 16 * GameView.density() / 2;

        if(Utils.isInRect(x, y,
                GameView.getScreenWidth() / 3 + 64 * GameView.density() - 24 * GameView.density() / 2, tempY - staticSliderImageHeight / 2,
                GameView.getScreenWidth() / 3 + 64 * GameView.density() + GameView.getScreenWidth() * 4 / 9 + 24 * GameView.density() / 2, tempY - staticSliderImageHeight / 2 + 32 * GameView.density())){
            if(x > GameView.getScreenWidth() / 3 + 64 * GameView.density() + GameView.getScreenWidth() * 4 / 9 / 2){
                BattleState.inputCommand = Consts.INPUT_COMMAND_POINT;
            }else{
//                BattleState.inputCommand = Consts.INPUT_COMMAND_DRAG;
            }
            PlayerStats.updateAccountStats();
        }
        switch(BattleState.inputCommand){
            case Consts.INPUT_COMMAND_POINT :
                sliderX = (int) (GameView.getScreenWidth() / 3 + 64 * GameView.density() + GameView.getScreenWidth() * 4 / 9 - 24 * GameView.density() / 2);
                break;

            case Consts.INPUT_COMMAND_DRAG :
                sliderX = (int) (GameView.getScreenWidth() / 3 + 64 * GameView.density() - 24 * GameView.density() / 2);
                break;
        }
        return true;
    }

    /**
     * Handles touch input when action is releasing and is not the first finger touching the screen.
     */
    @Override
    public boolean onActionPointerUp(MotionEvent event) {
        return true;
    }

    /**
     * Is called when state is to be changed.
     */
    @Override
    public void endState() {

    }
}
