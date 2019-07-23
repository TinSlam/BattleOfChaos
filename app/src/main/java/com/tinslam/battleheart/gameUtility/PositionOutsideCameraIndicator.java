package com.tinslam.battleheart.gameUtility;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.utils.shapes.Line2d;

public class PositionOutsideCameraIndicator{
    private Unit unit;
    private float radius = 24 * GameView.density();
    private int image;
    private static float tangent = (float) GameView.getScreenHeight() / GameView.getScreenWidth();

    public PositionOutsideCameraIndicator(Unit unit){
        this.unit = unit;

        image = PlayerStats.getUnitPositionIndicator(unit.getClass().getSimpleName());
    }

    public void render(float cameraX, float cameraY) {
        float point[];
        float offset;
        Line2d line = new Line2d(cameraX, cameraY, unit.getCollisionBox().centerX(), unit.getCollisionBox().centerY());
        if(Math.abs(line.getTangent()) > tangent){
            offset = Math.abs((float) Math.sin(Math.atan(line.getTangent()))) * radius;
            if(unit.getY() > cameraY){
                // Bottom line.
                point = line.getIntersectionPoint(Line2d.Y_COORD, cameraY + GameView.getScreenHeight() / 2 - offset);
            }else{
                // Top line.
                point = line.getIntersectionPoint(Line2d.Y_COORD, cameraY - GameView.getScreenHeight() / 2 + offset);
            }
        }else{
            offset = (float) Math.cos(Math.atan(line.getTangent())) * radius;
            if(unit.getX() > cameraX){
                // Right line.
                point = line.getIntersectionPoint(Line2d.X_COORD, cameraX + GameView.getScreenWidth() / 2 - offset);
            }else{
                // Left line.
                point = line.getIntersectionPoint(Line2d.X_COORD, cameraX - GameView.getScreenWidth() / 2 + offset);
            }
        }
        point[0] -= cameraX - GameView.getScreenWidth() / 2;
        point[1] -= cameraY - GameView.getScreenHeight() / 2;
        MyGL2dRenderer.drawLabel((int) (point[0] - radius), (int) (point[1] - radius),
                (int) (2 * radius), (int) (2 * radius), image, 255);
    }

    public Unit getUnit() {
        return unit;
    }
}
