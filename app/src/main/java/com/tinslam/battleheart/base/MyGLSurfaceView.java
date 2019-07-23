package com.tinslam.battleheart.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.tinslam.battleheart.elements3D.KingdomManager;
import com.tinslam.battleheart.elements3D.Label;
import com.tinslam.battleheart.entities.Entity3D;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.shapes.Line3d;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The view of the 3d part of the game.
 */
public class MyGLSurfaceView extends GLSurfaceView implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener{
    private final GestureDetector mGestureDetector;
    private volatile boolean ignoreCameraTranslation;

    private volatile float[] touchVector;
    private volatile float lastX, lastY;

    private float density;

    private static final Object translationLock = new Object();

    /**
     * Constructor.
     * @param context The activity.
     */
    public MyGLSurfaceView(Context context){
        super(context);

        // Create a GestureDetector
        mGestureDetector = new GestureDetector(context, this);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(this);
    }

    /**
     * Handles touch inputs.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        try{
            mGestureDetector.onTouchEvent(event);
            if(event != null){
                float x, y;

                switch(event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN :
                        synchronized(translationLock){
                            x = event.getX();
                            y = event.getY();

                            ignoreCameraTranslation = false;

                            lastX = x;
                            lastY = y;
                        }
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN :
                        synchronized(translationLock){
                            ignoreCameraTranslation = true;
                            x = event.getX(1);
                            y = event.getY(1);

                            float[] position = MyGLRenderer.convertPixelToOpenGLCoordinates(x, y);
                            if(Label.onActionDown(position[0], position[1])) break;

                            touchVector = new float[] {x - event.getX(), y - event.getY()};
                        }
                        break;

                    case MotionEvent.ACTION_MOVE :
                        if(event.getPointerCount() == 1){
                            synchronized(translationLock){
                                if(ignoreCameraTranslation) break;

                                // Translate
                                x = event.getX();
                                y = event.getY();

                                if(Utils.distance(x, y, lastX, lastY) < 50){
                                    MyGLRenderer.translateCamera(((x - lastX) / density / 2f / MyGLRenderer.getCameraSpeed()), (y - lastY) / density / 2f / MyGLRenderer.getCameraSpeed(), 0);
                                }

                                lastX = x;
                                lastY = y;
                            }
                        }else if(event.getPointerCount() == 2){
                            float[] newVector = new float[] {event.getX(1) - event.getX(), event.getY(1) - event.getY()};
                            float size1 = Utils.vectorSize(touchVector);
                            float size2 = Utils.vectorSize(newVector);
                            // Zoom
                            if(!KingdomManager.isImmerseMode()){
                                float distance = size2 - size1;
//                                MyGLRenderer.rotateCamera(-MyGLRenderer.getAngle(), 0, 0, 1);
                                MyGLRenderer.translateCamera(0, distance / density / Consts.GL_CAMERA_ZOOM_SPEED, -distance / density / Consts.GL_CAMERA_ZOOM_SPEED);
//                                MyGLRenderer.rotateCamera(MyGLRenderer.getAngle(), 0, 0, 1);
                            }

                            // Rotate
                            float dotProduct = Utils.dotProduct(newVector, touchVector);
                            float cos = dotProduct / size1 / size2;
                            if(cos > 1 || cos < -1) break;
                            float angle = (float) Math.toDegrees(Math.acos(cos));
                            if(Utils.vectorAngle(newVector) < Utils.vectorAngle(touchVector)) angle *= -1;
                            MyGLRenderer.rotateCamera(angle, 0, 0, 1);

                            MyGLRenderer.setAngle(MyGLRenderer.getAngle() + angle);
                            touchVector = newVector;
                        }
                        break;

                    case MotionEvent.ACTION_UP :

                        break;

                    case MotionEvent.ACTION_POINTER_UP :

                        break;
                }
                return true;
            }else{
                return super.onTouchEvent(null);
            }
        }catch(Exception e){
            final Writer stringBuffSync = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringBuffSync);
            e.printStackTrace(printWriter);
            String stacktrace = stringBuffSync.toString();
            printWriter.close();

            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TinSlam");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            Date date = new Date();
            String filename = dateFormat.format(date) + ".txt";

            // Write the file into the folder
            File reportFile = new File(dir, filename);
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(reportFile);
                fileWriter.append(stacktrace);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(1);
            return super.onTouchEvent(null);
        }
    }

    /**
     * Sets the 3d renderer.
     */
    // Hides superclass method.
    public void setRenderer(MyGLRenderer renderer, float density){
        this.density = density;
        super.setRenderer(renderer);
    }

    /**
     * Handles touch input.
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event){
        return false;
    }

    /**
     * Handles touch input.
     */
    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    /**
     * Handles touch input.
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    /**
     * Handles touch input.
     */
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    /**
     * Handles touch input.
     */
    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    /**
     * Handles touch input.
     */
    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        float[] position = MyGLRenderer.convertPixelToOpenGLCoordinates(x, y);
        if(Label.onActionUp(position[0], position[1])) return true;

        Line3d worldCoordinates = MyGLRenderer.getWorldVector(x, y);

        Entity3D prioritizedModel = null;

        synchronized(Entity3D.getEntitiesLock()){
            for(Entity3D model : Entity3D.getEntities()){
                if(!model.isClickable()) continue;
                if(model.isTouched(worldCoordinates)){
                    if(prioritizedModel == null){
                        prioritizedModel = model;
                    }else{
                        if(Utils.distance(model.getCenterX(), model.getCenterY(), model.getCenterZ(),
                                MyGLRenderer.cameraX, MyGLRenderer.cameraY, MyGLRenderer.cameraZ) <
                                Utils.distance(prioritizedModel.getCenterX(), prioritizedModel.getCenterY(), prioritizedModel.getCenterZ(),
                                        MyGLRenderer.cameraX, MyGLRenderer.cameraY, MyGLRenderer.cameraZ)) prioritizedModel = model;
                    }
                }
            }
        }
        if(prioritizedModel != null){
            final Entity3D finalPrioritizedModel = prioritizedModel;
            new TimedTaskRepeat(10) {
                float width = finalPrioritizedModel.getWidth();
                float height = finalPrioritizedModel.getHeight();
                float depth = finalPrioritizedModel.getDepth();
                float z = finalPrioritizedModel.getZ();
                int counter = 0;

                @Override
                public boolean checkCondition() {
                    return counter == 50;
                }

                @Override
                public void performAction() {
                    if(counter == 37){
                        new Event() {
                            @Override
                            public void performAction() {
                                finalPrioritizedModel.onActionUp();
                            }
                        };
                    }

                    if(counter >= 25){
                        finalPrioritizedModel.setZ(finalPrioritizedModel.getZ() - finalPrioritizedModel.getDepth() / 99);
                        finalPrioritizedModel.setWidth(finalPrioritizedModel.getWidth() * 100 / 99);
                        finalPrioritizedModel.setHeight(finalPrioritizedModel.getHeight() * 100 / 99);
                        finalPrioritizedModel.setDepth(finalPrioritizedModel.getDepth() * 100 / 99);
                    }else{
                        finalPrioritizedModel.setZ(finalPrioritizedModel.getZ() + finalPrioritizedModel.getDepth() / 100);
                        finalPrioritizedModel.setWidth(finalPrioritizedModel.getWidth() * 99 / 100);
                        finalPrioritizedModel.setHeight(finalPrioritizedModel.getHeight() * 99 / 100);
                        finalPrioritizedModel.setDepth(finalPrioritizedModel.getDepth() * 99 / 100);
                    }
                    counter++;
                }

                @Override
                public void end() {
                    finalPrioritizedModel.setZ(z);
                    finalPrioritizedModel.setWidth(width);
                    finalPrioritizedModel.setHeight(height);
                    finalPrioritizedModel.setDepth(depth);
                }
            };
            return true;
        }

        return false;
    }

    /**
     * Handles touch input.
     */
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    /**
     * Handles touch input.
     */
    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    /**
     * Handles touch input.
     */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
