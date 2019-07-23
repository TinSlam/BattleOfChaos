package com.tinslam.battleheart.UI.graphics.renderingAssistants;

import android.opengl.Matrix;

import com.tinslam.battleheart.base.MyGL2dRenderer;

/**
 * A class that renders an image.
 */
public class ImageRenderer{
    private int x, y;
    private int width, height;
    private int image;

    /**
     * Constructor.
     */
    public ImageRenderer(int x, int y, int width, int height, int image){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    /**
     * Renders the image.
     */
    public void render(float xOffset, float yOffset){
        MyGL2dRenderer.drawLabel((int) (x + xOffset), (int) (y + yOffset), width, height, image, 255);
    }

    /**
     * Renders the image.
     */
    public void renderFlipped(float xOffset, float yOffset){
        float[] matrix = {(int) (x + xOffset + width), (int) (y + yOffset), -width, height, 0};
        MyGL2dRenderer.drawLabel(image, 255, matrix);
    }

    /**
     * @return The left.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The top.
     */
    public int getY() {
        return y;
    }

    /**
     * @return The image.
     */
    public int getImage(){
        return image;
    }

    /**
     * Sets the x position of the renderer.
     */
    public void setX(int x) {
        this.x = x;
    }

    public void setImage(int image){
        this.image = image;
    }
}
