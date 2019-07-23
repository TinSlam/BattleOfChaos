package com.tinslam.battleheart.elements3D;

import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.shapes.Square;

import java.util.ArrayList;

/**
 * The 2d UI elements of the 3D state.
 */
public abstract class Label{
    private static final Object labelsLock = new Object();
    private static ArrayList<Label> labels = new ArrayList<>();

    private float x, y, z = 0;
    private float scaleX, scaleY;
    private float width, height;
    private int texture;
    private int[] vboId, iboId;
    private int iboLength = Square.getIboLength();

    /**
     * Constructor.
     */
    public Label(float x, float y, float width, float height, int texture){
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;

        scaleX = width;
        scaleY = height;

        this.texture = texture;

        vboId = Square.getVboId();
        iboId = Square.getIboId();

        addLabel(this);
    }

    public abstract boolean onDown();

    public abstract boolean onUp();

    /**
     * Checks whether any label has been touched.
     */
    public static boolean onActionDown(float x, float y){
        for(Label l : labels){
            if(Utils.isInRect(x, -y, l.getX(), l.getY(), l.getX2(), l.getY2())){
                return l.onDown();
            }
        }

        return false;
    }

    /**
     * Checks whether any label has been touched.
     */
    public static boolean onActionUp(float x, float y){
        for(Label l : labels){
            if(Utils.isInRect(x, -y, l.getX(), l.getY(), l.getX2(), l.getY2())){
                return l.onUp();
            }
        }

        return false;
    }

    /**
     * Updates the 3D model for all labels to prevent crashing when surface is re-created.
     */
    public static void updateModels(){
        for(Label x : labels){
            x.updateModel();
        }
    }

    /**
     * Updates the 3D model for the label to prevent crashing when surface is re-created.
     */
    private void updateModel(){
        vboId = Square.getVboId();
        iboId = Square.getIboId();
    }

    /**
     * @return An ArrayList of all the existing labels.
     */
    public static ArrayList<Label> getLabels() {
        return labels;
    }

    /**
     * @return A lock that controls the synchronization of all actions performed on the labels list.
     */
    public static Object getLabelsLock() {
        return labelsLock;
    }

    /**
     * Draws the label.
     */
    public void draw(){
        MyGLRenderer.drawLabel(this, texture);
    }

    /**
     * Adds the label to the labels list.
     */
    private static void addLabel(final Label label){
        new Event() {
            @Override
            public void performAction() {
                    labels.add(label);
            }
        };
    }

    /**
     * Removes the label from the labels list.
     */
    public static void removeLabel(final Label label){
        new Event() {
            @Override
            public void performAction() {
                    labels.remove(label);
            }
        };
    }

    /**
     * @return The number of vertices to draw.
     */
    public int getIboLength(){
        return iboLength;
    }

    /**
     * @return The x position.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x position.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return The y position.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y position.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return The x scale.
     */
    public float getScaleX() {
        return scaleX;
    }

    /**
     * @return The y scale.
     */
    public float getScaleY() {
        return scaleY;
    }

    /**
     * @return The Width.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @return The height.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * @return The texture.
     */
    public int getTexture() {
        return texture;
    }

    /**
     * Sets the texture.
     */
    public void setTexture(int texture) {
        this.texture = texture;
    }

    /**
     * @return The vbo id.
     */
    public int[] getVboId() {
        return vboId;
    }

    /**
     * @return The ibo id.
     */
    public int[] getIboId() {
        return iboId;
    }

    /**
     * @return The right.
     */
    private float getX2(){
        return x + width;
    }

    /**
     * @return The bottom.
     */
    private float getY2(){
        return y + height;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}