package com.tinslam.battleheart.base;

import android.opengl.GLES20;

import com.tinslam.battleheart.utils.FileManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * A class that stores the information of a 3d model.
 */
public class ModelData{
    private FloatBuffer vbo;
    private ShortBuffer ibo;
    private final int[] vboId = new int[1];
    private final int[] iboId = new int[1];
    private float[] vboFloatArray;
    private short[] iboShortArray;
    private float width, height, depth;

    /**
     * Constructor.
     * @param model The resource id of the model.
     */
    public ModelData(int model){
        FileManager.parseObj(model, this);
        initialize();
    }

    /**
     * Creates the VBO and the IBO.
     */
    private void initialize(){
        createVBO();
        createIBO();
    }

    /**
     * Creates the VBO.
     */
    private void createVBO(){
        vbo = ByteBuffer
                .allocateDirect(vboFloatArray.length * MyGLRenderer.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vbo.put(vboFloatArray).position(0);

        GLES20.glGenBuffers(1, vboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vbo.capacity() * MyGLRenderer.BYTES_PER_FLOAT,
                vbo, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Creates the IBO.
     */
    private void createIBO(){
        ibo = ByteBuffer
                .allocateDirect(iboShortArray.length * MyGLRenderer.BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
                .asShortBuffer();
        ibo.put(iboShortArray).position(0);

        GLES20.glGenBuffers(1, iboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboId[0]);

        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo.capacity() * MyGLRenderer.BYTES_PER_SHORT,
                ibo, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Clears the vbo and the ibo.
     */
    public void clear(){
        GLES20.glDeleteBuffers(1, iboId, 0);
        ibo.clear();
        GLES20.glDeleteBuffers(1, vboId, 0);
        vbo.clear();
    }

    /**
     * Sets the VBO.
     */
    public void setVBO(float[] vbo) {
        this.vboFloatArray = vbo;
    }

    /**
     * Sets the IBO.
     */
    public void setIBO(short[] ibo) {
        this.iboShortArray= ibo;
    }

    /**
     * @return The width of the model. Is the maximum length along the x axis.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width of the model.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @return The height of the model. Is the maximum length along the y axis.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of the model.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * @return The depth of the model. Is the maximum length along the z axis.
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Sets the depth of the model.
     */
    public void setDepth(float depth) {
        this.depth = depth;
    }

    /**
     * @return The id of the generated VBO.
     */
    int[] getVboId() {
        return vboId;
    }

    /**
     * @return The id of the generated IBO.
     */
    int[] getIboId() {
        return iboId;
    }

    /**
     * @return The length of the IBO.
     */
    int getIboShortArrayLength() {
        return iboShortArray.length;
    }
}
