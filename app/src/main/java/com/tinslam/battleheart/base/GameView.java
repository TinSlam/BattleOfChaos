package com.tinslam.battleheart.base;

import android.content.Context;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.HealthBarRenderer;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.UI.graphics.visualEffects.VisualEffect;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.gameUtility.TouchHandler;
import com.tinslam.battleheart.states.DashboardState;
import com.tinslam.battleheart.states.LoadingState;
import com.tinslam.battleheart.states.State;

/**
 * The view of the 2d part of the game.
 */
public class GameView{
    private GameThread thread;
    private static Context context;
    private static State state;
    private static LoadingState loadingState;
    private static boolean loading = false;
    private static int width, height;
    public static GameView gameView = null;
    private static Rect screenRect;
    private static boolean active = false;

    /**
     * Constructor.
     */
    public GameView(Context context, int width, int height){
        setScreenSize(width, height);

        GameView.context = context;

        thread = new GameThread();
    }

    /**
     *
     * @return The main view.
     */
    public static GameView gameView(){
        return gameView;
    }

    /**
     * Resumes or starts the main thread when game gains focus.
     */
    public void surfaceCreated() {
        if (gameView == null) {
            gameView = this;
//            Image.loadImages();
            AnimationLoader.loadAnimations();
            PlayerStats.load();
            state = new DashboardState();
            state.initConfirmationPanel();
            state.startState();
            active = true;
//            thread.setRunning(true);
//            thread.start();
        } else {
            active = true;
//            thread.setRunning(true);
            GameThread.resumeThread();
        }
    }

    /**
     * Handles back key events.
     */
    public void handleBackPressed(){
        if(loading) return;
        state.handleBackPressed();
    }

    /**
     * Handles key events.
     */
    public void handleKeyEvent(KeyEvent event){
        if(loading) return;
        state.handleKeyEvent(event);
    }

    /**
     * Pauses the game thread when game loses focus.
     */
    public void surfaceDestroyed() {
        state.surfaceDestroyed();
        GameThread.pauseThread();
        active = false;
    }

    /**
     * Handles touch events.
     */
    public boolean onTouchEvent(MotionEvent event) {
        return loading || (state.onTouchEvent(event));
    }

    /**
     * Is called on every frame.
     */
    public void update(){
        if(loading){
            loadingState.tick();
        }else{
            state.tickState();
        }
    }

    /**
     * Is called on every frame. Handles the drawings.
     */
    public void draw(){
        if(loading){
            loadingState.render();
        }else{
            state.renderState();
        }
    }

    /**
     * @return The context of the view.
     */
    public static Context Context() {
        return MyGL2dRenderer.getContext();
    }

    /**
     * @return Device's pixel density. Not sure how accurate this is.
     */
    public static float density(){ return MyGL2dRenderer.getContext().getResources().getDisplayMetrics().density; }

    /**
     * @return The width of the screen in pixels.
     */
    public static int getScreenWidth() {
        return MyGL2dRenderer.getWidth();
    }

    /**
     * @return The height of the screen in pixels.
     */
    public static int getScreenHeight() {
        return MyGL2dRenderer.getHeight();
    }

    /**
     * @return The current state.
     */
    public static State getState() {
        return state;
    }

    public static boolean stateChangeOnCD = false;

    /**
     * Changes the state of the game. Ends the current state and starts the next state.
     * @param state The state to change to.
     */
    public static void setState(final State state, final String lastState, final LoadingState loadingState) {
        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.solid_red, 255);
//        final LoadingState ld = new LoadingState(getState());
        stateChangeOnCD = true;
        GameView.state.end();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                while(!flag){
                    flag = TouchHandler.getTouchHandlers().isEmpty() &&
                            Entity.getEntities().isEmpty() &&
                            VisualEffect.getVisualEffects().isEmpty() &&
                            TextRenderer.isEmpty() &&
                            HealthBarRenderer.isEmpty();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                ld.finishLoading();
                GameView.state = state;
//                new Event() {
//                    @Override
//                    public void performAction() {
                        state.initConfirmationPanel();
                        GameView.state.startState();
//                        if(loadingState != null) loadingState.finishLoading();
                        GameView.state.setLastState(lastState);
                        stateChangeOnCD = false;
//                    }
//                };
            }
        }).start();
    }

    public static void setState(State state, String lastState){
        setState(state, lastState, null);
    }

    /**
     *
     * @return A rectangle that contains the device's screen.
     */
    public static Rect getScreenRect(){
        return screenRect;
    }

    /**
     * Determines whether the game is running in the foreground or not.
     * False means the user has the game running in the background.
     */
    public static boolean isActive(){
        return active;
    }

    public static void loading(LoadingState loadingState){
        GameView.loadingState = loadingState;
        loading = true;
    }

    public static void loadingFinished(){
        loadingState = null;
        loading = false;
    }

    /**
     * @return The string with the given id from the strings file.
     */
    public static String string(int id) {
        return context.getString(id);
    }

    public void setScreenSize(int width, int height){
        GameView.width = width;
        GameView.height = height;

        screenRect = new Rect(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight());
    }
}
