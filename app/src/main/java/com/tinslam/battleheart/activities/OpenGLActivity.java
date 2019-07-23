package com.tinslam.battleheart.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.base.MyGLSurfaceView;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.states.DashboardState;

/**
 * The activity that uses OpenGL 2.
 */
public class OpenGLActivity extends Activity {
    private MyGLSurfaceView glSurfaceView;
    public static OpenGLActivity openGLActivity;

	/**
	 * Is called on create.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		openGLActivity = this;
		glSurfaceView = new MyGLSurfaceView(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(glSurfaceView);

		glSurfaceView.setEGLContextClientVersion(2);

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		MyGLRenderer renderer = new MyGLRenderer(this);
		glSurfaceView.setRenderer(renderer, displayMetrics.density);
	}

	/**
	 * Is called on resume.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		glSurfaceView.onResume();
	}

	/**
	 * Is called on pause.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		glSurfaceView.onPause();
	}

	/**
	 * Is called when the back button is pressed.
	 */
	@Override
	public void onBackPressed() {
		if(GameView.stateChangeOnCD) return;
		ActivityManager.switchToActivity(OpenGLActivity.openGLActivity, OpenGL2dActivity.class);
		GameView.setState(new DashboardState(), "");
	}
}