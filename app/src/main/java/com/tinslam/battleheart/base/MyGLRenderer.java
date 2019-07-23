package com.tinslam.battleheart.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;

import com.tinslam.battleheart.elements3D.KingdomManager;
import com.tinslam.battleheart.elements3D.Label;
import com.tinslam.battleheart.elements3D.Model3D;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.utils3D.ShaderHelper;
import com.tinslam.battleheart.R;
import com.tinslam.battleheart.entities.Entity3D;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.FileManager;
import com.tinslam.battleheart.utils.shapes.Line3d;
import com.tinslam.battleheart.utils.shapes.Square;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer{
    private static final Object renderLock = new Object();

    private final Context openGLActivity;
    @SuppressLint("StaticFieldLeak")
    private static MyGLRenderer myGLRenderer;

    private volatile static float angle;

    private static final float[] modelMatrix = new float[16];
    private static final float[] viewMatrix = new float[16];
    private static final float[] projectionMatrix = new float[16];
    private static final float[] mvpMatrix = new float[16];
    private final float[] lightModelMatrix = new float[16];

    private static int textureUniform;

    private static int positionAttribute;
    private static int normalAttribute;
    private static int textureAttribute;

    private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
    private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
    private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";
    private static final String TEXTURE_UNIFORM = "u_Texture";
    private static final String AMBIENT_LIGHTING_UNIFORM = "ambientLighting";

    private static final String POSITION_ATTRIBUTE = "a_Position";
    private static final String NORMAL_ATTRIBUTE = "a_Normal";
    private static final String TEXTURE_ATTRIBUTE = "a_TexCoordinate";

    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int NORMAL_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int TEXTURE_DATA_SIZE_IN_ELEMENTS = 2;

    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_SHORT = 2;

    private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS + TEXTURE_DATA_SIZE_IN_ELEMENTS)
            * BYTES_PER_FLOAT;

    private final float[] lightPosInModelSpace = new float[] { 0, 0, 0, 1 };

    private final float[] lightPosInWorldSpace = new float[4];

    private final float[] lightPosInEyeSpace = new float[4];

    private int program;
    private int lightProgram;
    private int labelProgram;

    private static int width = 0, height = 0;

    static float cameraX;
    static float cameraY;
    static float cameraZ;

    private static float projectionNear = 1f;
    private static float immerseZoomFactor = 2;

    private static boolean firstTime = true;

    /**
     * Constructor.
     */
    public MyGLRenderer(Context openGLActivity){
        super();

        MyGLRenderer.myGLRenderer = this;
        this.openGLActivity = openGLActivity;
    }

    /**
     * Is called whenever the surface is created.
     */
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config){
        try{
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            // Use culling to remove back faces.
//            GLES20.glEnable(GLES20.GL_CULL_FACE);

            // Enable depth testing
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            setupPrograms();

            Model3D.loadModels();
            TextureData.loadTextures();
            Square.loadShape();

            if(firstTime){
                if(KingdomManager.isImmerseMode()){
                    setCameraPositionToImmerseMode();
                }else{
                    resetCameraPosition();
                }
                KingdomManager.loadKingdom();
            }else{
                Label.updateModels();
            }
            firstTime = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void setCameraPositionToImmerseMode(){
        // Position of the camera.
        final float eyeX = 60;
        final float eyeY = 60;
        final float eyeZ = -1;
        cameraX = eyeX;
        cameraY = eyeY;
        cameraZ = eyeZ;

        // Looking towards the point :
        final float lookX = 60;
        final float lookY = 0;
        final float lookZ = -1;
        angle = 0;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0;
        final float upY = 0;
        final float upZ = -1;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        setFrustumToImmersionMode();
    }

    private static void resetFrustum(){
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = projectionNear;
        final float far = 100.0f;
        Matrix.frustumM(projectionMatrix, 0, left, ratio, bottom, top, near, far);
    }

    private static void setFrustumToImmersionMode(){
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = projectionNear;
        final float far = 100.0f;
        Matrix.frustumM(projectionMatrix, 0, left / immerseZoomFactor, ratio / immerseZoomFactor, bottom / immerseZoomFactor, top / immerseZoomFactor, near, far);
    }

    public static void resetCameraPosition(){
        final float eyeX = Consts.CAMERA_EYE_X;
        final float eyeY = Consts.CAMERA_EYE_Y;
        final float eyeZ = Consts.CAMERA_EYE_Z;
        cameraX = eyeX;
        cameraY = eyeY;
        cameraZ = eyeZ;

        final float lookX = Consts.CAMERA_LOOK_X;
        final float lookY = Consts.CAMERA_LOOK_Y;
        final float lookZ = Consts.CAMERA_LOOK_Z;
        angle = Consts.CAMERA_ANGLE;

        final float upX = Consts.CAMERA_UP_X;
        final float upY = Consts.CAMERA_UP_Y;
        final float upZ = Consts.CAMERA_UP_Z;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        resetFrustum();
    }

    /**
     * Sets up the programs. Shaders and all.
     */
    private void setupPrograms(){
        final String vertexShader = FileManager.readTextFileFromRawResource(openGLActivity, R.raw.per_pixel_vertex_shader_tex_and_light);
        final String fragmentShader = FileManager.readTextFileFromRawResource(openGLActivity, R.raw.per_pixel_fragment_shader_tex_and_light);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, TEXTURE_ATTRIBUTE});

        // Define a simple shader program for our point.
        final String pointVertexShader = FileManager.readTextFileFromRawResource(openGLActivity, R.raw.point_vertex_shader);
        final String pointFragmentShader = FileManager.readTextFileFromRawResource(openGLActivity, R.raw.point_fragment_shader);

        final int pointVertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        lightProgram = ShaderHelper.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[] {POSITION_ATTRIBUTE});

        // Define a program for the UI.
        final String labelVertexShader = FileManager.readTextFileFromRawResource(openGLActivity, R.raw.per_pixel_vertex_shader_no_light);
        final String labelFragmentShader = FileManager.readTextFileFromRawResource(openGLActivity, R.raw.per_pixel_fragment_shader_no_light);

        final int labelVertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, labelVertexShader);
        final int labelFragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, labelFragmentShader);
        labelProgram = ShaderHelper.createAndLinkProgram(labelVertexShaderHandle, labelFragmentShaderHandle,
                new String[] {POSITION_ATTRIBUTE, TEXTURE_ATTRIBUTE});
    }

    /**
     * Translates the camera.
     */
    static void translateCamera(float x, float y, float z){
        synchronized(renderLock){
            float[] pos = new float[4];
            float[] rotMatrix = new float[16];
            Matrix.setRotateM(rotMatrix, 0, -getAngle(), 0, 0, 1);
            Matrix.multiplyMV(pos, 0, rotMatrix, 0, new float[] {x, y, z, 1}, 0);
            if(!KingdomManager.isImmerseMode()){
                cameraX -= pos[0];
                cameraY -= pos[1];
                cameraZ -= pos[2];
                Matrix.translateM(viewMatrix, 0, pos[0], pos[1], pos[2]);
            }else{
                // Playing around.
                float xOffset = width / height / immerseZoomFactor + 1;
                cameraX -= pos[0];
                cameraY -= pos[1];
                cameraZ -= pos[2];
                if(KingdomManager.isEmpty(cameraX - xOffset, cameraX + xOffset, cameraY - xOffset, cameraY + xOffset)){
                    Matrix.translateM(viewMatrix, 0, pos[0], pos[1], pos[2]);
                    return;
                }else{
                    cameraX += pos[0];
                    cameraY += pos[1];
                    cameraZ += pos[2];
                }
                float xTemp = pos[0];
                float yTemp = pos[1];
                if(pos[0] > pos[1]) pos[1] = 0; else pos[0] = 0;
                cameraX -= pos[0];
                cameraY -= pos[1];
                cameraZ -= pos[2];
                if(KingdomManager.isEmpty(cameraX - xOffset, cameraX + xOffset, cameraY - xOffset, cameraY + xOffset)){
                    Matrix.translateM(viewMatrix, 0, pos[0], pos[1], pos[2]);
                    return;
                }else{
                    cameraX += pos[0];
                    cameraY += pos[1];
                    cameraZ += pos[2];
                }
                if(xTemp > yTemp){
                    pos[0] = 0;
                    pos[1] = yTemp;
                }else{
                    pos[0] = xTemp;
                    pos[1] = 0;
                }
                cameraX -= pos[0];
                cameraY -= pos[1];
                cameraZ -= pos[2];
                if(KingdomManager.isEmpty(cameraX - xOffset, cameraX + xOffset, cameraY - xOffset, cameraY + xOffset)){
                    Matrix.translateM(viewMatrix, 0, pos[0], pos[1], pos[2]);
                }else{
                    cameraX += pos[0];
                    cameraY += pos[1];
                    cameraZ += pos[2];
                }
            }
        }
    }

    /**
     * Rotates the camera.
     */
    static void rotateCamera(float angle, float x, float y, float z){
        synchronized(renderLock){
            Matrix.translateM(viewMatrix, 0, cameraX, cameraY, cameraZ);
            Matrix.rotateM(viewMatrix, 0, angle, x, y, z);
            Matrix.translateM(viewMatrix, 0, -cameraX, -cameraY, -cameraZ);
        }
    }

    /**
     * Converts mouse coordinates to world coordinates.
     */
    static Line3d getWorldVector(float x, float y){
        float[] point1 = new float[4];
        float[] point2 = new float[4];

        GLU.gluUnProject(x, height - y, 0, viewMatrix, 0, projectionMatrix, 0, new int[] {0, 0, width, height}, 0, point1, 0);
        GLU.gluUnProject(x, height - y, 1, viewMatrix, 0, projectionMatrix, 0, new int[] {0, 0, width, height}, 0, point2, 0);

        for(int i = 0; i < 3; i++){
            point1[i] /= point1[3];
            point2[i] /= point2[3];
        }

        return new Line3d(point1[0], point1[1], point1[2], point2[0], point2[1], point2[2]);
    }

    static float[] getScreenPosition(float x, float y, float z){
        float[] position = new float[3];

        GLU.gluProject(x, y, z, viewMatrix, 0, projectionMatrix, 0, new int[] {0, 0, width, height}, 0, position, 0);

//        for(int i = 0; i < 3; i++){
//            position[i] /= position[3];
//        }

        return position;
    }

    static float[] convertPixelToOpenGLCoordinates(float x, float y){
        float[] position = new float[2];

        position[0] = x - width / 2;
        position[1] = y - height / 2;

        position[0] /= width / 2;
        position[1] /= height / 2;

        return position;
    }

    /**
     * Is called whenever surface is changed.
     */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height){
        try{
            MyGLRenderer.width = width;
            MyGLRenderer.height = height;

            if(KingdomManager.isImmerseMode()){
                setFrustumToImmersionMode();
            }else{
                resetFrustum();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Draws the frame.
     */
    @Override
    public void onDrawFrame(GL10 unused){
        try{
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glUniform1i(textureUniform, 0);

            GLES20.glUseProgram(program);
            drawEntities();

            GLES20.glUseProgram(labelProgram);
            drawLabels();

            GLES20.glUseProgram(lightProgram);
            drawLight();

            TimedTask.tick();
            TimedTaskRepeat.tick();
            Event.runEvents();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void drawLabels(){
        int mvpMatrixUniform = GLES20.glGetUniformLocation(labelProgram, MVP_MATRIX_UNIFORM);
        int mvMatrixUniform = GLES20.glGetUniformLocation(labelProgram, MV_MATRIX_UNIFORM);
        textureUniform = GLES20.glGetUniformLocation(labelProgram, TEXTURE_UNIFORM);
        positionAttribute = GLES20.glGetAttribLocation(labelProgram, POSITION_ATTRIBUTE);
        textureAttribute = GLES20.glGetAttribLocation(labelProgram, TEXTURE_ATTRIBUTE);

        synchronized(Label.getLabelsLock()){
            for(Label x : Label.getLabels()){
                Matrix.setIdentityM(modelMatrix, 0);
                Matrix.translateM(modelMatrix, 0, x.getX(), x.getY(), -projectionNear);
                Matrix.scaleM(modelMatrix, 0, x.getScaleX(), x.getScaleY(), 1);

//                Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
                System.arraycopy(modelMatrix, 0, mvpMatrix, 0, 16);

                GLES20.glUniformMatrix4fv(mvMatrixUniform, 1, false, mvpMatrix, 0);

//                Matrix.multiplyMM(modelMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
                System.arraycopy(modelMatrix, 0, mvpMatrix, 0, 16);

                GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

                x.draw();
            }
        }
    }

    private void drawEntities(){
        int mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
        int mvMatrixUniform = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
        int lightPosUniform = GLES20.glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);
        int ambientLightingUniform = GLES20.glGetUniformLocation(program, AMBIENT_LIGHTING_UNIFORM);
        textureUniform = GLES20.glGetUniformLocation(program, TEXTURE_UNIFORM);
        positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
        normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
        textureAttribute = GLES20.glGetAttribLocation(program, TEXTURE_ATTRIBUTE);

        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 32f, 20f, -12.0f);
        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);

        GLES20.glUniform3f(lightPosUniform, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
        GLES20.glUniform1f(ambientLightingUniform, KingdomManager.ambientLighting);

        synchronized(renderLock){
            synchronized(Entity3D.getEntitiesLock()){
                for(Entity3D x : Entity3D.getEntities()){
                    Matrix.setIdentityM(modelMatrix, 0);
                    Matrix.translateM(modelMatrix, 0, x.getX() + x.getWidth() / 2, x.getY() + x.getHeight() / 2, x.getZ() + x.getDepth() / 2);
                    Matrix.rotateM(modelMatrix, 0, x.getRotX(), 1, 0, 0);
                    Matrix.rotateM(modelMatrix, 0, x.getRotY(), 0, 1, 0);
                    Matrix.rotateM(modelMatrix, 0, x.getRotZ(), 0, 0, 1);
                    Matrix.scaleM(modelMatrix, 0, x.getScaleX(), x.getScaleY(), x.getScaleZ());

                    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

                    GLES20.glUniformMatrix4fv(mvMatrixUniform, 1, false, mvpMatrix, 0);

                    Matrix.multiplyMM(modelMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
                    System.arraycopy(modelMatrix, 0, mvpMatrix, 0, 16);

                    GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

                    x.draw();
                }
            }
        }
    }

    public static void drawLabel(Label label, int texture){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, label.getVboId()[0]);

        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, 0);

        GLES20.glEnableVertexAttribArray(textureAttribute);
        GLES20.glVertexAttribPointer(textureAttribute, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, label.getIboId()[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, label.getIboLength(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Draws a 3d model with the given matrix.
     */
    public static void drawModel(ModelData modelData, int texture){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, modelData.getVboId()[0]);

        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, STRIDE, 0);

        GLES20.glEnableVertexAttribArray(normalAttribute);
        GLES20.glVertexAttribPointer(normalAttribute, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);

        GLES20.glEnableVertexAttribArray(textureAttribute);
        GLES20.glVertexAttribPointer(textureAttribute, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, STRIDE, (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, modelData.getIboId()[0]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, modelData.getIboShortArrayLength(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Draws the light.
     */
    private void drawLight(){
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(lightProgram, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(lightProgram, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, lightModelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    /**
     * @return The context.
     */
    public static Context getContext(){
        return myGLRenderer.openGLActivity;
    }

    /**
     * @return The device density.
     */
    public static float density(){ return getContext().getResources().getDisplayMetrics().density; }

    /**
     * @return Camera angle.
     */
    static float getAngle() {
        return angle;
    }

    /**
     * Sets the camera angle.
     */
    static void setAngle(float angle) {
        MyGLRenderer.angle = angle;
    }

    /**
     * @return Camera translation speed.
     */
    static float getCameraSpeed(){
        return (float) (Consts.GL_CAMERA_SPEED / Math.sqrt(Math.abs(cameraZ)));
    }
}