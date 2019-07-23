package com.tinslam.battleheart.activities;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.base.MyGL2dSurfaceView;
import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.base.MyGLSurfaceView;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.states.DashboardState;
import com.tinslam.battleheart.utils.CustomizedExceptionHandler;

/**
 * The activity that uses OpenGL 2.
 */
public class OpenGL2dActivity extends Activity {
    private MyGL2dSurfaceView gl2dSurfaceView;
    public static OpenGL2dActivity openGL2dActivity;

    /**
     * Is called on create.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openGL2dActivity = this;
        gl2dSurfaceView = new MyGL2dSurfaceView(this);

        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(gl2dSurfaceView);

        gl2dSurfaceView.setEGLContextClientVersion(2);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        MyGL2dRenderer renderer = new MyGL2dRenderer(this);
        gl2dSurfaceView.setRenderer(renderer);
        gl2dSurfaceView.setPreserveEGLContextOnPause(true);
    }

    /**
     * Is called on resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        gl2dSurfaceView.onResume();
        if(GameView.gameView() != null) GameView.gameView().surfaceCreated();
    }

    /**
     * Is called on pause.
     */
    @Override
    protected void onPause() {
        super.onPause();
        gl2dSurfaceView.onPause();
        if(MyGL2dRenderer.myGL2dRenderer.game != null) MyGL2dRenderer.myGL2dRenderer.game.surfaceDestroyed();
    }

    /**
     * Is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if(MyGL2dRenderer.myGL2dRenderer.game != null) MyGL2dRenderer.myGL2dRenderer.game.handleBackPressed();
    }
}