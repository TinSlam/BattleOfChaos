package com.tinslam.battleheart.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * The view of the 3d part of the game.
 */
public class MyGL2dSurfaceView extends GLSurfaceView{
    /**
     * Constructor.
     * @param context The activity.
     */
    public MyGL2dSurfaceView(Context context){
        super(context);
    }

    /**
     * Handles touch inputs.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(MyGL2dRenderer.myGL2dRenderer.game != null) return MyGL2dRenderer.myGL2dRenderer.game.onTouchEvent(event);
        return true;
    }

    /**
     * Sets the 3d renderer.
     */
    // Hides superclass method.
    public void setRenderer(MyGL2dRenderer renderer){
        super.setRenderer(renderer);
    }
}
