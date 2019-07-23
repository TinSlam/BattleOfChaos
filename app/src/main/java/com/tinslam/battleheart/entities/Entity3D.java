package com.tinslam.battleheart.entities;

import com.tinslam.battleheart.base.ModelData;
import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.shapes.Box;
import com.tinslam.battleheart.utils.shapes.Line3d;

import java.util.ArrayList;

/**
 * The class that handles all the 3d entities.
 */
public abstract class Entity3D {
    private static final Object entitiesLock = new Object();
    private static ArrayList<Entity3D> entities = new ArrayList<>();

    private ModelData modelData;
    private int texture;
    private float x, y, z;
    private float rotX, rotY, rotZ;
    private float scaleX, scaleY, scaleZ;
    private float width, height, depth;
    private boolean isClickable = false;
    private Box boundingBox;

    /**
     * Constructor.
     */
    public Entity3D(float x, float y, float z, float rotX, float rotY, float rotZ, float width, float height, float depth){
        loadModel();

        this.x = x;
        this.y = y;
        this.z = z;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.width = width;
        this.height = height;
        this.depth = depth;

        scaleX = width / modelData.getWidth();
        scaleY = height / modelData.getHeight();
        scaleZ = depth / modelData.getDepth();

        boundingBox = new Box(x, y, z, width, height, depth);

        addEntity(this);
    }

    /**
     * Is called when clicked.
     */
    public abstract void onActionUp();

    /**
     * Is called in the constructor. Sets the model and the texture.
     */
    public abstract void loadModel();

    /**
     * Draws the 3d model.
     */
    public void draw(){
        MyGLRenderer.drawModel(modelData, texture);
    }

    /**
     * Checks if the entity is touched.
     */
    public boolean isTouched(Line3d line){
        return Utils.lineBoxIntersection(line, getBoundingBox());
    }

    /**
     * Adds entity to the entities list.
     */
    private static void addEntity(final Entity3D model){
        new Event() {
            @Override
            public void performAction() {
                entities.add(model);
            }
        };
    }

    /**
     * Removes the entity from the entities list.
     */
    public static void removeEntity(final Entity3D model){
        new Event() {
            @Override
            public void performAction() {
                    entities.remove(model);
            }
        };
    }

    /**
     * Sets the 3d model of the entity.
     */
    public void setModelData(ModelData modelData){
        this.modelData = modelData;
    }

    /**
     * Sets the texture of the entity.
     */
    public void setTexture(int texture){
        this.texture = texture;
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
     * @return The z position.
     */
    public float getZ() {
        return z;
    }

    /**
     * Sets the z position.
     */
    public void setZ(float z) {
        this.z = z;
    }

    /**
     * @return The x rotation.
     */
    public float getRotX() {
        return rotX;
    }

    /**
     * @return The y rotation.
     */
    public float getRotY() {
        return rotY;
    }

    /**
     * @return The z rotation.
     */
    public float getRotZ() {
        return rotZ;
    }

    /**
     * @return The width.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width;
     */
    public void setWidth(float width) {
        scaleX = width / this.width * scaleX;
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
        scaleY = height / this.height * scaleY;
        this.height = height;
    }

    /**
     * @return The depth.
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Sets the depth.
     */
    public void setDepth(float depth) {
        scaleZ = depth / this.depth * scaleZ;
        this.depth = depth;
    }

    /**
     * @return A lock that controls the synchronization of the actions performed on the entities list.
     */
    public static Object getEntitiesLock() {
        return entitiesLock;
    }

    /**
     * @return An ArrayList of all the existing 3d entities.
     */
    public static ArrayList<Entity3D> getEntities() {
        return entities;
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
     * @return The z scale.
     */
    public float getScaleZ() {
        return scaleZ;
    }

    /**
     * Checks whether the entity is clickable or not.
     */
    public boolean isClickable() {
        return isClickable;
    }

    /**
     * Sets the clickability of the entity.
     */
    protected void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    /**
     * @return The bounding box of the entity.
     */
    private Box getBoundingBox(){
        return boundingBox;
    }

    /**
     * @return The texture of the entity.
     */
    public int getTexture() {
        return texture;
    }

    /**
     * @return The center of the entity along the x axis.
     */
    public float getCenterX() {
        return x + width / 2;
    }

    /**
     * @return The center of the entity along the y axis.
     */
    public float getCenterY() {
        return y + height / 2;
    }

    /**
     * @return The center of the entity along the z axis.
     */
    public float getCenterZ() {
        return z + depth / 2;
    }
}