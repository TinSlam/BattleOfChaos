package com.tinslam.battleheart.utils.shapes;

/**
 * A class that holds information of a 3d line.
 */
public class Line3d{
    public static final byte X_COORD = 0, Y_COORD = 1, Z_COORD = 2;

    private float x0, y0, z0;
    private float m, n, p;

    /**
     * Constructor.
     */
    public Line3d(float x1, float y1, float z1, float x2, float y2, float z2){
        x0 = x1;
        y0 = y1;
        z0 = z1;
        m = x1 - x2;
        n = y1 - y2;
        p = z1 - z2;
    }

    /**
     *
     * @param givenPoint Indicates which component is given.
     * @param coord The given component.
     * @return The intersection of the line and the line passed in. In other words returns the point of the line that has the given component.
     */
    public float[] getIntersectionPoint(byte givenPoint, float coord){
        float[] point = new float[3];
        float t;

        switch(givenPoint){
            case X_COORD :
                t = (coord - x0) / m;
                point[0] = coord;
                point[1] = n * t + y0;
                point[2] = p * t + z0;
                break;

            case Y_COORD :
                t = (coord - y0) / n;
                point[0] = m * t + x0;
                point[1] = coord;
                point[2] = p * t + z0;
                break;

            case Z_COORD :
                t = (coord - z0) / p;
                point[0] = m * t + x0;
                point[1] = n * t + y0;
                point[2] = coord;
                break;
        }

        return point;
    }
}
