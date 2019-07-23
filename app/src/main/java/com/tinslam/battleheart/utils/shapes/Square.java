package com.tinslam.battleheart.utils.shapes;

import android.opengl.GLES20;

import com.tinslam.battleheart.base.MyGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * OpenGL square class. Contains the VBO and the IBO.
 */
public class Square{
    private static int[] vboId = new int[1], iboId = new int[1];

    private static final float[] vertices = {
        0, 0, 0, 0, 1,
        0, 1, 0, 0, 0,
        1, 0, 0, 1, 1,
        1, 1, 0, 1, 0
    };

    private static final short[] indices = {
            0, 2, 1, 1, 2, 3
    };

    public static void loadShape(){
        clear();
        createVbo();
        createIbo();
    }

    private static void clear(){
        GLES20.glDeleteBuffers(1, vboId, 0);
        GLES20.glDeleteBuffers(1, iboId, 0);
    }

    private static void createVbo(){
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * MyGLRenderer.BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vbo = bb.asFloatBuffer();
        vbo.put(vertices);
        vbo.position(0);

        GLES20.glGenBuffers(1, vboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vbo.capacity() * MyGLRenderer.BYTES_PER_FLOAT,
                vbo, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private static void createIbo(){
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * MyGLRenderer.BYTES_PER_SHORT);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer ibo = dlb.asShortBuffer();
        ibo.put(indices);
        ibo.position(0);

        GLES20.glGenBuffers(1, iboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboId[0]);

        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo.capacity() * MyGLRenderer.BYTES_PER_SHORT,
                ibo, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public static int[] getVboId(){
        return vboId;
    }

    public static int[] getIboId(){
        return iboId;
    }

    public static int getIboLength() {
        return indices.length;
    }
}
