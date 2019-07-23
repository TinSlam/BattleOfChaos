package com.tinslam.battleheart.utils.shapes;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.tinslam.battleheart.utils.Utils;

public class Rectangle{
    private Rect rect;
    private float angle = 0;
    private Matrix matrix;
    private float[] v1 = new float[4], v2 = new float[4], v3 = new float[4], v4 = new float[4];


    public Rectangle(int x, int y, int width, int height, float angle){
        rect = new Rect(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        setAngle(angle);

        matrix = new Matrix();
    }

    public void render(Canvas canvas){

    }

    public Matrix getRenderMatrix(){
        matrix.reset();
        matrix.preTranslate(rect.left, rect.top);
        matrix.preRotate(angle, rect.width() / 2, rect.height() / 2);
        return matrix;
    }

    public float[] getMatrixInfo(){
        return new float[] {rect.left, rect.top, rect.width(), rect.height(), angle};
    }

    public void translate(int dx, int dy){
        rect.set(rect.left + dx, rect.top + dy, rect.right + dx, rect.bottom + dy);
    }

    public void position(int x, int y) {
        rect.set(x - rect.width() / 2, y - rect.height() / 2, x + rect.width() / 2, y + rect.height() / 2);
    }

    public void setAngle(float angle){
        this.angle = -angle;
    }

    public float getAngle(){
        return -angle;
    }

    public float[] getV1(){
        float[] point = getRotatedPoint(1);
        v1[0] = point[0];
        v1[1] = point[1];
        point = getRotatedPoint(2);
        v1[2] = point[0];
        v1[3] = point[1];
        return v1;
    }

    public float[] getV2(){
        float[] point = getRotatedPoint(2);
        v2[0] = point[0];
        v2[1] = point[1];
        point = getRotatedPoint(3);
        v2[2] = point[0];
        v2[3] = point[1];
        return v2;
    }

    public float[] getV3(){
        float[] point = getRotatedPoint(3);
        v3[0] = point[0];
        v3[1] = point[1];
        point = getRotatedPoint(4);
        v3[2] = point[0];
        v3[3] = point[1];
        return v3;
    }

    public float[] getV4(){
        float[] point = getRotatedPoint(4);
        v4[0] = point[0];
        v4[1] = point[1];
        point = getRotatedPoint(1);
        v4[2] = point[0];
        v4[3] = point[1];
        return v4;
    }

    private float[] getRotatedPoint(int vertex){
        float[] point = new float[2];

        int cx = rect.centerX();
        int cy = rect.centerY();

        translate(-cx, -cy);

        switch(vertex){
            case 1 :
                point[0] = (float) (Math.cos(Math.toRadians(angle)) * rect.left - Math.sin(Math.toRadians(angle)) * rect.top);
                point[1] = (float) (Math.sin(Math.toRadians(angle)) * rect.left + Math.cos(Math.toRadians(angle)) * rect.top);
                break;

            case 2 :
                point[0] = (float) (Math.cos(Math.toRadians(angle)) * rect.right - Math.sin(Math.toRadians(angle)) * rect.top);
                point[1] = (float) (Math.sin(Math.toRadians(angle)) * rect.right + Math.cos(Math.toRadians(angle)) * rect.top);
                break;

            case 3 :
                point[0] = (float) (Math.cos(Math.toRadians(angle)) * rect.right - Math.sin(Math.toRadians(angle)) * rect.bottom);
                point[1] = (float) (Math.sin(Math.toRadians(angle)) * rect.right + Math.cos(Math.toRadians(angle)) * rect.bottom);
                break;

            case 4 :
                point[0] = (float) (Math.cos(Math.toRadians(angle)) * rect.left - Math.sin(Math.toRadians(angle)) * rect.bottom);
                point[1] = (float) (Math.sin(Math.toRadians(angle)) * rect.left + Math.cos(Math.toRadians(angle)) * rect.bottom);
                break;
        }

        translate(cx, cy);

        point[0] += cx;
        point[1] += cy;

        return point;
    }

    public static boolean intersect(Rectangle r1, Rect r2){
        // Top Line :
        float[] line = r1.getV1();

        float[] vector = new float[] {line[2] - line[0], line[3] - line[1]};

        float[] rotatedPoint = new float[] {r2.left, r2.top};
        if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
            rotatedPoint = new float[] {r2.right, r2.top};
            if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                rotatedPoint = new float[] {r2.right, r2.bottom};
                if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                    rotatedPoint = new float[] {r2.left, r2.bottom};
                    if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                        return false;
                    }
                }
            }
        }

        // Right Line :
        line = r1.getV2();

        vector = new float[] {line[2] - line[0], line[3] - line[1]};

        rotatedPoint = new float[] {r2.left, r2.top};
        if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
            rotatedPoint = new float[] {r2.right, r2.top};
            if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                rotatedPoint = new float[] {r2.right, r2.bottom};
                if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                    rotatedPoint = new float[] {r2.left, r2.bottom};
                    if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                        return false;
                    }
                }
            }
        }

        // Bottom Line :
        line = r1.getV3();

        vector = new float[] {line[2] - line[0], line[3] - line[1]};

        rotatedPoint = new float[] {r2.left, r2.top};
        if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
            rotatedPoint = new float[] {r2.right, r2.top};
            if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                rotatedPoint = new float[] {r2.right, r2.bottom};
                if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                    rotatedPoint = new float[] {r2.left, r2.bottom};
                    if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                        return false;
                    }
                }
            }
        }

        // Left Line :
        line = r1.getV4();

        vector = new float[] {line[2] - line[0], line[3] - line[1]};

        rotatedPoint = new float[] {r2.left, r2.top};
        if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
            rotatedPoint = new float[] {r2.right, r2.top};
            if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                rotatedPoint = new float[] {r2.right, r2.bottom};
                if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                    rotatedPoint = new float[] {r2.left, r2.bottom};
                    if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean intersect(Rectangle r1, Rectangle r2){
        // Top Line :
        float[] line = r1.getV1();

        float[] vector = new float[] {line[2] - line[0], line[3] - line[1]};

        float[] rotatedPoint = r2.getRotatedPoint(1);
        if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
            rotatedPoint = r2.getRotatedPoint(2);
            if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                rotatedPoint = r2.getRotatedPoint(3);
                if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                    rotatedPoint = r2.getRotatedPoint(4);
                    if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                        return false;
                    }
                }
            }
        }

        // Right Line :
        line = r1.getV2();

        vector = new float[] {line[2] - line[0], line[3] - line[1]};

        rotatedPoint = r2.getRotatedPoint(1);
        if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
            rotatedPoint = r2.getRotatedPoint(2);
            if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                rotatedPoint = r2.getRotatedPoint(3);
                if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                    rotatedPoint = r2.getRotatedPoint(4);
                    if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                        return false;
                    }
                }
            }
        }

        // Bottom Line :
        line = r1.getV3();

        vector = new float[] {line[2] - line[0], line[3] - line[1]};

        rotatedPoint = r2.getRotatedPoint(1);
        if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
            rotatedPoint = r2.getRotatedPoint(2);
            if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                rotatedPoint = r2.getRotatedPoint(3);
                if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                    rotatedPoint = r2.getRotatedPoint(4);
                    if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                        return false;
                    }
                }
            }
        }

        // Left Line :
        line = r1.getV4();

        vector = new float[] {line[2] - line[0], line[3] - line[1]};

        rotatedPoint = r2.getRotatedPoint(1);
        if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
            rotatedPoint = r2.getRotatedPoint(2);
            if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                rotatedPoint = r2.getRotatedPoint(3);
                if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                    rotatedPoint = r2.getRotatedPoint(4);
                    if(Utils.crossProduct(vector, new float[] {rotatedPoint[0] - line[0], rotatedPoint[1] - line[1]})[2] < 0){
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
