package com.tinslam.battleheart.UI.buttons;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class NullButton extends Button{

    /**
     * Constructor.
     */
    public NullButton() {
        super(null, null, true);
    }

    /**
     * Resizes the button based on its center.
     *
     * @param newWidth
     * @param newHeight
     */
    @Override
    public void resizeImage(int newWidth, int newHeight) {

    }

    /**
     * Translates the button.
     *
     * @param dx
     * @param dy
     */
    @Override
    public void translate(int dx, int dy) {

    }

    /**
     * Renders the button.
     *
     */
    @Override
    public void render() {

    }

    /**
     * Is called when button is pressed.
     */
    @Override
    public boolean onDown() {
        return false;
    }

    /**
     * Is called when button is released.
     */
    @Override
    public boolean onUp() {
        return false;
    }

    /**
     * Checks whether the button contains the point (x, y).
     *
     * @param x
     * @param y
     */
    @Override
    public boolean isTouched(int x, int y) {
        return false;
    }

    /**
     * Repositions the button.
     *
     * @param x
     * @param y
     */
    @Override
    public void setPosition(int x, int y) {

    }

    /**
     * @return The x position of the button.
     */
    @Override
    public int getX() {
        return 0;
    }

    /**
     * @return The y position of the button.
     */
    @Override
    public int getY() {
        return 0;
    }

    /**
     * @return The width of the button.
     */
    @Override
    public int getWidth() {
        return 0;
    }

    /**
     * @return The height of the button.
     */
    @Override
    public int getHeight() {
        return 0;
    }
}
