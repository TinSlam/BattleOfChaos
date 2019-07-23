package com.tinslam.battleheart.utils.shapes;

/**
 * A class that holds information of a 2d line.
 */
public class Line2d{
    public static final byte X_COORD = 0, Y_COORD = 1;

    private float a, b, y0;

    /**
     * Constructor.
     */
    public Line2d(float x1, float y1, float x2, float y2){
        a = (x1 == x2) ? Float.MAX_VALUE : (y2 - y1) / (x2 - x1);
        b = y1 - a * x1;
        y0 = y1;
    }

    /**
     *
     * @param givenPoint Indicates which component is given.
     * @param coord The given component.
     * @return The intersection of the line and the point passed in. In other words returns the point of the line that has the given component.
     */
    public float[] getIntersectionPoint(byte givenPoint, float coord){
        float[] point = new float[2];

        switch(givenPoint){
            case X_COORD :
                point[0] = coord;
                if(a == Float.MAX_VALUE){
                    point[1] = y0;
                }else{
                    point[1] = a * coord + b;
                }
                break;

            case Y_COORD :
                if(a == Float.MAX_VALUE){
                    point[0] = Float.MAX_VALUE;
                }else{
                    point[0] = (coord - b) / a;
                }
                point[1] = coord;
                break;
        }

        return point;
    }

    public float getTangent() {
        return a;
    }
}
