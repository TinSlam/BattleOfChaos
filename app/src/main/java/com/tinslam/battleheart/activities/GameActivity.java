package com.tinslam.battleheart.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.utils.CustomizedExceptionHandler;

/**
 * Sets the game up. Also changes the activity when needed.
 */
public class GameActivity extends Activity {
    private static GameActivity gameActivity;

    /**
     * Sets up the game. That's pretty much it.
     * @param savedInstanceState Idk.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameActivity.setGameActivity(this);

        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler());

        gameActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gameActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager windowManager = ((WindowManager)getSystemService(Context.WINDOW_SERVICE));
        if(windowManager != null){
            Display d;
            d = windowManager.getDefaultDisplay();
            final Point size = new Point();
            d.getSize(size);
//            setContentView(new GameView(gameActivity, size.x, size.y));
        }
    }

    /**
     * Listener for key input.
     * @param event The key event.
     * @return I don't really know.
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch(event.getAction()){
            case KeyEvent.ACTION_UP :
                try{
                    GameView.gameView().handleKeyEvent(event);
                }catch(Exception ignored){
                    ignored.printStackTrace();
                }
                break;
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * Listener for pressing the back button.
     */
    @Override
    public void onBackPressed() {
        GameView.gameView().handleBackPressed();
    }

    /**
     * Changes the activity.
     * @param gameActivity The activity to change to.
     */
    private static void setGameActivity(GameActivity gameActivity){
        GameActivity.gameActivity = gameActivity;
    }

    public static Activity gameActivity() {
        return gameActivity;
    }
}