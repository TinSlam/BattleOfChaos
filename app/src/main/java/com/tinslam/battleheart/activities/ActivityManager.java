package com.tinslam.battleheart.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

/**
 * A class that manages activities.
 */
public class ActivityManager{
    /**
     * Closes all the activities and kills the game.
     */
    public static void closeGame(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(OpenGLActivity.openGLActivity != null) OpenGLActivity.openGLActivity.finishAndRemoveTask();
//            if(GameActivity.gameActivity() != null) GameActivity.gameActivity().finishAndRemoveTask();
            if(OpenGL2dActivity.openGL2dActivity != null) OpenGL2dActivity.openGL2dActivity.finishAndRemoveTask();
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(OpenGLActivity.openGLActivity != null) OpenGLActivity.openGLActivity.finishAffinity();
//                if(GameActivity.gameActivity() != null) GameActivity.gameActivity().finishAffinity();
                if(OpenGL2dActivity.openGL2dActivity != null) OpenGL2dActivity.openGL2dActivity.finishAffinity();
            }else{
                if(OpenGLActivity.openGLActivity != null) OpenGLActivity.openGLActivity.finish();
//                if(GameActivity.gameActivity() != null) GameActivity.gameActivity().finish();
                if(OpenGL2dActivity.openGL2dActivity != null) OpenGL2dActivity.openGL2dActivity.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * Switches to another activity.
     * @param currentActivity The current activity.
     * @param targetActivity The target activity class.
     */
    public static void switchToActivity(Activity currentActivity, Class targetActivity){
        Intent openMainActivity = new Intent(currentActivity, targetActivity);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        currentActivity.startActivityIfNeeded(openMainActivity, 0);
    }
}
