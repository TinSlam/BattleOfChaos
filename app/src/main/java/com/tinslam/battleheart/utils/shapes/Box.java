package com.tinslam.battleheart.utils.shapes;

/**
 * A class that holds information of a box.
 */
public class Box{
    private float x, y, z;
    private float width, height, depth;

    /**
     * Constructor.
     */
    public Box(float x, float y, float z, float width, float height, float depth){
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public float getX2(){
        return x + width;
    }

    public float getY2(){
        return y + height;
    }

    public float getZ2(){
        return z + depth;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }
}