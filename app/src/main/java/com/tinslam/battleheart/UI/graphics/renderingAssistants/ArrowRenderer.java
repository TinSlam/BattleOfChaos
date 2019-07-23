package com.tinslam.battleheart.UI.graphics.renderingAssistants;

import android.graphics.Canvas;

/**
 * Draws an arrow. Honestly I've no idea why this is still there. El O El.
 */
public class ArrowRenderer {
    public void render(Canvas canvas){
//        float srcX = src.getX();
//        float srcY = src.getY();
//        if(Utils.distance(x2, y2, srcX, srcY) < src.getImage().getWidth() / 2) return;
//
//        float angle = (float) (Math.atan2(y2 - srcY, x2 - srcX));
//        angle += Math.toRadians(90);
//        float s = (float) Math.sin(angle);
//        float c = (float) Math.cos(angle);
//        float rectWidth = 8 * GameView.density();
//        float arrowStartBeforePoint = 12 * GameView.density();
//        float arrowMaxWidth = 16 * GameView.density();
//
//        Path p = new Path();
//        p.moveTo(srcX + rectWidth / 2 * c, srcY + rectWidth / 2 * s);
//        p.lineTo(x2 + rectWidth / 2 * c - arrowStartBeforePoint * s, y2 + rectWidth / 2 * s + arrowStartBeforePoint * c);
//        p.lineTo(x2 - rectWidth / 2 * c - arrowStartBeforePoint * s, y2 - rectWidth / 2 * s + arrowStartBeforePoint * c);
//        p.lineTo(srcX - rectWidth / 2 * c, srcY - rectWidth / 2 * s);
//        p.close();
//
//        Path p2 = new Path();
//        p2.moveTo(x2 + arrowMaxWidth * c - arrowStartBeforePoint * s, y2 + arrowMaxWidth * s + arrowStartBeforePoint * c);
//        p2.lineTo(x2, y2);
//        p2.lineTo(x2 - arrowMaxWidth * c - arrowStartBeforePoint * s, y2 - arrowMaxWidth * s + arrowStartBeforePoint * c);
//        p2.close();
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        canvas.drawPath(p, paint);
//        canvas.drawPath(p2, paint);
//        canvas.drawCircle(srcX, srcY, rectWidth / 2, paint);
    }
}
