package com.tinslam.battleheart.utils.shapes;

/**
 * A class that has has methods for using a circle.
 */
public class Circle{
    private float x, y, radius;

    /**
     * The constructor.
     * @param x The x position of the center of the circle.
     * @param y The y position of the center of the circle.
     * @param radius The radius of the circle.
     */
    public Circle(float x, float y, float radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    /**
     *
     * @return The x position of the center of the circle.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x position of the center of the circle.
     * @param x
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     *
     * @return The y position of the center of the circle.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y position of the center of the circle.
     * @param y
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     *
     * @return The radius of the circle.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the circle.
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }
}
