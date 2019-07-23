package com.tinslam.battleheart.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.elements3D.Label;
import com.tinslam.battleheart.elements3D.Model3D;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity3D;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.FileManager;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.TimedTaskRepeat;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.shapes.Line3d;
import com.tinslam.battleheart.utils.shapes.Square;
import com.tinslam.battleheart.utils.utils3D.ShaderHelper;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGL2dRenderer implements GLSurfaceView.Renderer{
    private static final Object renderLock = new Object();

    private boolean threeDimensional = false;

    public GameView game;

    private final Context openGL2dActivity;
    @SuppressLint("StaticFieldLeak")
    public static MyGL2dRenderer myGL2dRenderer;

    private volatile static float angle3d;

    private static final float[] modelMatrix3d = new float[16];
    private static final float[] viewMatrix3d = new float[16];
    private static final float[] projectionMatrix3d = new float[16];
    private static final float[] mvpMatrix3d = new float[16];
    private final float[] lightModelMatrix3d = new float[16];

    private volatile static float angle;

    private static final float[] modelMatrix = new float[16];
    private static final float[] viewMatrix = new float[16];
    private static final float[] projectionMatrix = new float[16];
    private static final float[] mvpMatrix = new float[16];

    private static int textureUniform3d;

    private static int positionAttribute3d;
    private static int normalAttribute3d;
    private static int textureAttribute3d;

    private static int mvMatrixUniform;
    private static int mvpMatrixUniform;
    private static int textureUniform;

    private static int positionAttribute;
    private static int normalAttribute;
    private static int textureAttribute;

    private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
    private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
    private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";
    private static final String TEXTURE_UNIFORM = "u_Texture";

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

    private final float[] lightPosInModelSpace3d = new float[] { 0, 0, 0, 1 };

    private final float[] lightPosInWorldSpace3d = new float[4];

    private final float[] lightPosInEyeSpace3d = new float[4];

    private int program;
    private int lightProgram;
    private static int labelProgram;

    private static int width = 0, height = 0;

    static float cameraX, cameraX3d;
    static float cameraY, cameraY3d;
    static float cameraZ, cameraZ3d;

    private static float projectionNear = 1;

    private static boolean firstTime = true;

    private static final Object buffersLock = new Object();
    private static ArrayList<int[]> buffersToRemove = new ArrayList<>();

    /**
     * Constructor.
     */
    public MyGL2dRenderer(Context openGL2dActivity){
        super();

        MyGL2dRenderer.myGL2dRenderer = this;
        this.openGL2dActivity = openGL2dActivity;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    /**
     * Is called whenever the surface is created.
     */
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config){
        try{
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

            // Use culling to remove back faces.
//            GLES20.glEnable(GLES20.GL_CULL_FACE);

            // Enable depth testing
//            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendColor(1, 1, 1, 1f);
//            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//            GLES20.glBlendFuncSeparate(GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA, GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//            GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);

            setupPrograms();

            Model3D.loadModels();
            TextureData.loadTextures();
            TextureData.load2dTextures();
            Square.loadShape();

            if(firstTime){
                // Position the eye in front of the origin.
                final float eyeX3d = Consts.CAMERA_EYE_X;
                final float eyeY3d = Consts.CAMERA_EYE_Y;
                final float eyeZ3d = Consts.CAMERA_EYE_Z;
                cameraX3d = eyeX3d;
                cameraY3d = eyeY3d;
                cameraZ3d = eyeZ3d;

                // We are looking toward the distance
                final float lookX3d = Consts.CAMERA_LOOK_X;
                final float lookY3d = Consts.CAMERA_LOOK_Y;
                final float lookZ3d = Consts.CAMERA_LOOK_Z;
                angle3d = Consts.CAMERA_ANGLE;

                // Set our up vector. This is where our head would be pointing were we holding the camera.
                final float upX3d = Consts.CAMERA_UP_X;
                final float upY3d = Consts.CAMERA_UP_Y;
                final float upZ3d = Consts.CAMERA_UP_Z;

                // Set the view matrix. This matrix can be said to represent the camera position.
                // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
                // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
                Matrix.setLookAtM(viewMatrix3d, 0, eyeX3d, eyeY3d, eyeZ3d, lookX3d, lookY3d, lookZ3d, upX3d, upY3d, upZ3d);

//                KingdomManager.loadKingdom();

                // Position the eye in front of the origin.
                final float eyeX = 0;
                final float eyeY = 0;
                final float eyeZ = 0f;
                cameraX = eyeX;
                cameraY = eyeY;
                cameraZ = eyeZ;

                // We are looking toward the distance
                final float lookX = 0;
                final float lookY = 0;
                final float lookZ = -5;
                angle = 0;

                // Set our up vector. This is where our head would be pointing were we holding the camera.
                final float upX = 0;
                final float upY = 1;
                final float upZ = 0;

                // Set the view matrix. This matrix can be said to represent the camera position.
                // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
                // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
                Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
            }else{
                AnimationLoader.loadAnimations();
                Label.updateModels();
            }
            firstTime = false;
            if(game != null) game.surfaceCreated();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sets up the programs. Shaders and all.
     */
    private void setupPrograms(){
        final String vertexShader = FileManager.readTextFileFromRawResource(openGL2dActivity, R.raw.per_pixel_vertex_shader_tex_and_light);
        final String fragmentShader = FileManager.readTextFileFromRawResource(openGL2dActivity, R.raw.per_pixel_fragment_shader_tex_and_light);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, TEXTURE_ATTRIBUTE});

        // Define a simple shader program for our point.
        final String pointVertexShader = FileManager.readTextFileFromRawResource(openGL2dActivity, R.raw.point_vertex_shader);
        final String pointFragmentShader = FileManager.readTextFileFromRawResource(openGL2dActivity, R.raw.point_fragment_shader);

        final int pointVertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        lightProgram = ShaderHelper.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[] {POSITION_ATTRIBUTE});

        final String labelVertexShader = FileManager.readTextFileFromRawResource(openGL2dActivity, R.raw.per_pixel_vertex_shader_no_light);
        final String labelFragmentShader = FileManager.readTextFileFromRawResource(openGL2dActivity, R.raw.per_pixel_fragment_shader_no_light);

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
            Matrix.setRotateM(rotMatrix, 0, -getAngle3d(), 0, 0, 1);
            Matrix.multiplyMV(pos, 0, rotMatrix, 0, new float[] {x, y, z, 1}, 0);
            cameraX3d -= pos[0];
            cameraY3d -= pos[1];
            cameraZ3d -= pos[2];
            Matrix.translateM(viewMatrix3d, 0, pos[0], pos[1], pos[2]);
        }
    }

    /**
     * Rotates the camera.
     */
    static void rotateCamera(float angle, float x, float y, float z){
        synchronized(renderLock){
            Matrix.translateM(viewMatrix3d, 0, cameraX3d, cameraY3d, cameraZ3d);
            Matrix.rotateM(viewMatrix3d, 0, angle, x, y, z);
            Matrix.translateM(viewMatrix3d, 0, -cameraX3d, -cameraY3d, -cameraZ3d);
        }
    }

    /**
     * Converts mouse coordinates to world coordinates.
     */
    static Line3d getWorldVector(float x, float y){
        float[] point1 = new float[4];
        float[] point2 = new float[4];

        GLU.gluUnProject(x, height - y, 0, viewMatrix3d, 0, projectionMatrix3d, 0, new int[] {0, 0, width, height}, 0, point1, 0);
        GLU.gluUnProject(x, height - y, 1, viewMatrix3d, 0, projectionMatrix3d, 0, new int[] {0, 0, width, height}, 0, point2, 0);

        for(int i = 0; i < 3; i++){
            point1[i] /= point1[3];
            point2[i] /= point2[3];
        }

        return new Line3d(point1[0], point1[1], point1[2], point2[0], point2[1], point2[2]);
    }

    static float[] getScreenPosition(float x, float y, float z){
        float[] position = new float[3];

        GLU.gluProject(x, y, z, viewMatrix3d, 0, projectionMatrix3d, 0, new int[] {0, 0, width, height}, 0, position, 0);

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
            if(threeDimensional){
                final float ratio3d = (float) width / height;
                final float left3d = -ratio3d;
                final float bottom3d = -1.0f;
                final float top3d = 1.0f;
                final float near3d = projectionNear;
                final float far3d = 100.0f;

                Matrix.frustumM(projectionMatrix3d, 0, left3d, ratio3d, bottom3d, top3d, near3d, far3d);
            }else{
                MyGL2dRenderer.width = width;
                MyGL2dRenderer.height = height;

                // Set the OpenGL viewport to the same size as the surface.
                GLES20.glViewport(0, 0, width, height);

                // Create a new perspective projection matrix. The height will stay the same
                // while the width will vary as per aspect ratio.
                final float ratio = (float) width / height;
                final float left = -ratio;
                final float right = ratio;
                final float bottom = -1.0f;
                final float top = 1.0f;
                final float near = 1 - 0.000001f;
                final float far = 10.0f;

                if(game == null){
                    game = new GameView(openGL2dActivity, width, height);
                    game.surfaceCreated();
                }

                game.setScreenSize(width, height);

                Matrix.frustumM(projectionMatrix, 0, left, ratio, bottom, top, near, far);
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
            if(threeDimensional){
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glUniform1i(textureUniform, 0);

                GLES20.glUseProgram(program);
                drawEntities();

//            GLES20.glUseProgram(labelProgram);
//            drawLabels();

                GLES20.glUseProgram(lightProgram);
                drawLight();
            }else{
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glUniform1i(textureUniform, 0);

                GLES20.glUseProgram(labelProgram);
                drawLabels();
                GameThread.tick();
                game.draw();
            }
            TimedTask.tick();
            TimedTaskRepeat.tick();
            Event.runEvents();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void drawLabels(){
        mvpMatrixUniform = GLES20.glGetUniformLocation(labelProgram, MVP_MATRIX_UNIFORM);
        mvMatrixUniform = GLES20.glGetUniformLocation(labelProgram, MV_MATRIX_UNIFORM);
        textureUniform = GLES20.glGetUniformLocation(labelProgram, TEXTURE_UNIFORM);
        positionAttribute = GLES20.glGetAttribLocation(labelProgram, POSITION_ATTRIBUTE);
        textureAttribute = GLES20.glGetAttribLocation(labelProgram, TEXTURE_ATTRIBUTE);
    }

    private void drawEntities(){
        int mvpMatrixUniform3d = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
        int mvMatrixUniform3d = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
        int lightPosUniform3d = GLES20.glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);
        textureUniform3d = GLES20.glGetUniformLocation(program, TEXTURE_UNIFORM);
        positionAttribute3d = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
        normalAttribute3d = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
        textureAttribute3d = GLES20.glGetAttribLocation(program, TEXTURE_ATTRIBUTE);

        Matrix.setIdentityM(lightModelMatrix3d, 0);
        Matrix.translateM(lightModelMatrix3d, 0, 32f, 20f, -12.0f);
        Matrix.multiplyMV(lightPosInWorldSpace3d, 0, lightModelMatrix3d, 0, lightPosInModelSpace3d, 0);
        Matrix.multiplyMV(lightPosInEyeSpace3d, 0, viewMatrix3d, 0, lightPosInWorldSpace3d, 0);

        GLES20.glUniform3f(lightPosUniform3d, lightPosInEyeSpace3d[0], lightPosInEyeSpace3d[1], lightPosInEyeSpace3d[2]);

        synchronized(renderLock){
            synchronized(Entity3D.getEntitiesLock()){
                for(Entity3D x : Entity3D.getEntities()){
                    Matrix.setIdentityM(modelMatrix3d, 0);
                    Matrix.translateM(modelMatrix3d, 0, x.getX() + x.getWidth() / 2, x.getY() + x.getHeight() / 2, x.getZ() + x.getDepth() / 2);
                    Matrix.rotateM(modelMatrix3d, 0, x.getRotX(), 1, 0, 0);
                    Matrix.rotateM(modelMatrix3d, 0, x.getRotY(), 0, 1, 0);
                    Matrix.rotateM(modelMatrix3d, 0, x.getRotZ(), 0, 0, 1);
                    Matrix.scaleM(modelMatrix3d, 0, x.getScaleX(), x.getScaleY(), x.getScaleZ());

                    Matrix.multiplyMM(mvpMatrix3d, 0, viewMatrix3d, 0, modelMatrix3d, 0);

                    GLES20.glUniformMatrix4fv(mvMatrixUniform3d, 1, false, mvpMatrix3d, 0);

                    Matrix.multiplyMM(modelMatrix3d, 0, projectionMatrix3d, 0, mvpMatrix3d, 0);
                    System.arraycopy(modelMatrix3d, 0, mvpMatrix3d, 0, 16);

                    GLES20.glUniformMatrix4fv(mvpMatrixUniform3d, 1, false, mvpMatrix3d, 0);

                    x.draw();
                }
            }
        }
    }

    public static void drawModel(ModelData modelData, int texture){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, modelData.getVboId()[0]);

        GLES20.glEnableVertexAttribArray(positionAttribute3d);
        GLES20.glVertexAttribPointer(positionAttribute3d, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, STRIDE, 0);

        GLES20.glEnableVertexAttribArray(normalAttribute3d);
        GLES20.glVertexAttribPointer(normalAttribute3d, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);

        GLES20.glEnableVertexAttribArray(textureAttribute3d);
        GLES20.glVertexAttribPointer(textureAttribute3d, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, STRIDE, (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, modelData.getIboId()[0]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, modelData.getIboShortArrayLength(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void drawLight(){
        final int pointMVPMatrixHandle3d = GLES20.glGetUniformLocation(lightProgram, "u_MVPMatrix");
        final int pointPositionHandle3d = GLES20.glGetAttribLocation(lightProgram, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle3d, lightPosInModelSpace3d[0], lightPosInModelSpace3d[1], lightPosInModelSpace3d[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle3d);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mvpMatrix3d, 0, viewMatrix3d, 0, lightModelMatrix3d, 0);
        Matrix.multiplyMM(mvpMatrix3d, 0, projectionMatrix3d, 0, mvpMatrix3d, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle3d, 1, false, mvpMatrix3d, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    private static float[] getOpenGLCoords(float x, float y){
        float ratio = (float) width / height;
        return new float[] {((2 * ratio) * x / width - ratio), -(2 * y / height - 1)};
    }

    private static float[] getOpenGLScales(float x, float y){
        float ratio = (float) width / height;
        return new float[] {x / width * (2 * ratio), y / height * 2};
    }

    public static void drawLabel(float x, float y, float width, float height, int texture, int alpha){
        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, (float) alpha / 255);
            GLES20.glBlendFunc(GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
        }
        Matrix.setIdentityM(modelMatrix, 0);
        float[] point = getOpenGLCoords(x, y);
        float[] point2 = getOpenGLScales(width, height);
        Matrix.translateM(modelMatrix, 0, point[0], point[1] - point2[1], -projectionNear);
        Matrix.scaleM(modelMatrix, 0, point2[0], point2[1], 1);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Square.getVboId()[0]);

        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, 0);

        GLES20.glEnableVertexAttribArray(textureAttribute);
        GLES20.glVertexAttribPointer(textureAttribute, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, Square.getIboId()[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, Square.getIboLength(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, 1);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public static void drawLabel(float x, float y, float width, float height, int texture, int alpha, int rotation){
        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, (float) alpha / 255);
            GLES20.glBlendFunc(GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
        }
        Matrix.setIdentityM(modelMatrix, 0);
        float[] point = getOpenGLCoords(x, y);
        float[] point2 = getOpenGLScales(width, height);
        Matrix.translateM(modelMatrix, 0, point[0], point[1] - point2[1], -projectionNear);
        Matrix.scaleM(modelMatrix, 0, point2[0], point2[1], 1);
        Matrix.translateM(modelMatrix, 0, point2[0], point2[1], 0);
        Matrix.rotateM(modelMatrix, 0, rotation, 0, 0, 1);
        Matrix.translateM(modelMatrix, 0, -point2[0], -point2[1], 0);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Square.getVboId()[0]);

        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, 0);

        GLES20.glEnableVertexAttribArray(textureAttribute);
        GLES20.glVertexAttribPointer(textureAttribute, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, Square.getIboId()[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, Square.getIboLength(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, 1);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public static void drawLabel(float x, float y, float width, float height, int texture, int alpha, int vboId, int iboId, int length, int offset){
        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, (float) alpha / 255);
            GLES20.glBlendFunc(GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
        }
        Matrix.setIdentityM(modelMatrix, 0);
        float[] point = getOpenGLCoords(x, y);
        float[] point2 = getOpenGLScales(width, height);
        Matrix.translateM(modelMatrix, 0, point[0], point[1] - point2[1], -projectionNear);
        Matrix.scaleM(modelMatrix, 0, point2[0], point2[1], 1);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, 0);

        GLES20.glEnableVertexAttribArray(textureAttribute);
        GLES20.glVertexAttribPointer(textureAttribute, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, length, GLES20.GL_UNSIGNED_SHORT, offset);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, 1);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public static void drawLabelRotationAtStart(int texture, int alpha, float[] matrixInfo){
        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, (float) alpha / 255);
            GLES20.glBlendFunc(GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
        }
        Matrix.setIdentityM(modelMatrix, 0);
        float[] point = getOpenGLCoords(matrixInfo[0], matrixInfo[1]);
        float[] point2 = getOpenGLScales(matrixInfo[2], matrixInfo[3]);
        Matrix.translateM(modelMatrix, 0, point[0], point[1] - point2[1], -projectionNear);
        Matrix.translateM(modelMatrix, 0, 0, point2[1] / 2, 0);
        Matrix.rotateM(modelMatrix, 0, -matrixInfo[4], 0, 0, 1);
        Matrix.translateM(modelMatrix, 0, 0, -point2[1] / 2, 0);
        Matrix.scaleM(modelMatrix, 0, point2[0], point2[1], 1);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Square.getVboId()[0]);

        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, 0);

        GLES20.glEnableVertexAttribArray(textureAttribute);
        GLES20.glVertexAttribPointer(textureAttribute, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, Square.getIboId()[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, Square.getIboLength(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, 1);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public static void drawLabel(int texture, int alpha, float[] matrixInfo){
        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, (float) alpha / 255);
            GLES20.glBlendFunc(GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
        }
        Matrix.setIdentityM(modelMatrix, 0);
        float[] point = getOpenGLCoords(matrixInfo[0], matrixInfo[1]);
        float[] point2 = getOpenGLScales(matrixInfo[2], matrixInfo[3]);
        Matrix.translateM(modelMatrix, 0, point[0], point[1] - point2[1], -projectionNear);
        Matrix.translateM(modelMatrix, 0, point2[0] / 2, point2[1] / 2, 0);
        Matrix.rotateM(modelMatrix, 0, -matrixInfo[4], 0, 0, 1);
        Matrix.translateM(modelMatrix, 0, -point2[0] / 2, -point2[1] / 2, 0);
        Matrix.scaleM(modelMatrix, 0, point2[0], point2[1], 1);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Square.getVboId()[0]);

        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, 0);

        GLES20.glEnableVertexAttribArray(textureAttribute);
        GLES20.glVertexAttribPointer(textureAttribute, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 20, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, Square.getIboId()[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, Square.getIboLength(), GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        if(alpha != 255){
            GLES20.glBlendColor(1, 1, 1, 1);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    /**
     * @return The context.
     */
    public static Context getContext(){
        return myGL2dRenderer.openGL2dActivity;
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
        MyGL2dRenderer.angle = angle;
    }

    /**
     * @return Camera translation speed.
     */
    static float getCameraSpeed(){
        return (float) (Consts.GL_CAMERA_SPEED / Math.sqrt(Math.abs(cameraZ)));
    }

    public static float getAngle3d() {
        return angle3d;
    }

    public static void setAngle3d(float angle3d) {
        MyGL2dRenderer.angle3d = angle3d;
    }

    public static float toOpenGLCoordsX(float x) {
        return (x - width / 2) / (width / 2);
    }

    public static float toOpenGLCoordsY(float y){
        return (y - height / 2) / (height / 2);
    }
}