package com.tinslam.battleheart.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.NameConsts;
import com.tinslam.battleheart.utils.constants.UnitConsts;
import com.tinslam.battleheart.utils.shapes.Box;
import com.tinslam.battleheart.utils.shapes.Circle;
import com.tinslam.battleheart.utils.shapes.Line2d;
import com.tinslam.battleheart.utils.shapes.Line3d;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * A class that comprises a collection of useful utility methods.
 */
public class Utils{
    public static String formatStringOneRadixPoint(float number){
        String string = String.format(Locale.US, "%.01f", number);
        if(string.endsWith(".0")) string = string.substring(0, string.length() - 2);
        return string;
    }

    public static String formatStringTwoRadixPoint(float number){
        String string = String.format(Locale.US, "%.02f", number);
        if(string.endsWith(".0")) string = string.substring(0, string.length() - 2);
        return string;
    }

    public static float getReducedDamage(float damage, float armor){
        return damage * calculateArmor(armor);
    }

    private static float calculateArmor(float armor){
        return (float) (1 - (0.05 * armor / (1 + 0.05 * Math.abs(armor))));
    }

    /**
     * Checks if point (x, y) is in the rectangle (x1, y1, x2, y2).
     */
    public static boolean isInRect(float x, float y, float x1, float y1, float x2, float y2){
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    /**
     * Checks if point (x, y) is in the rectangle rect.
     */
    public static boolean isInRect(float x, float y, Rect rect){
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    /**
     * Checks the intersection between a line and a box in the 3d space.
     * If the line intersects a face then they both intersect. 5 faces are enough to check.
     */
    public static boolean lineBoxIntersection(Line3d line, Box box){
        float[] point;

        // Bottom face.
        point = line.getIntersectionPoint(Line3d.Z_COORD, box.getZ2());
        if(isInRect(point[0], point[1], box.getX(), box.getY(), box.getX2(), box.getY2())) return true;

        // Top face.
        point = line.getIntersectionPoint(Line3d.Z_COORD, box.getZ());
        if(isInRect(point[0], point[1], box.getX(), box.getY(), box.getX2(), box.getY2())) return true;

        // Front face.
        point = line.getIntersectionPoint(Line3d.Y_COORD, box.getY2());
        if(isInRect(point[0], point[2], box.getX(), box.getZ(), box.getX2(), box.getZ2())) return true;

        // Back face.
        point = line.getIntersectionPoint(Line3d.Y_COORD, box.getY());
        if(isInRect(point[0], point[2], box.getX(), box.getZ(), box.getX2(), box.getZ2())) return true;

        // Left face.
        point = line.getIntersectionPoint(Line3d.X_COORD, box.getX());
        if(isInRect(point[1], point[2], box.getY(), box.getZ(), box.getY2(), box.getZ2())) return true;

        return false;
    }

    /**
     * Checks the intersection between a line and a rectangle.
     * If the line intersects a face then they both intersect. 5 faces are enough to check.
     */
    public static boolean lineRectIntersection(Line2d line, Rect rect){
        float[] point;

        // Left line.
        point = line.getIntersectionPoint(Line3d.X_COORD, rect.left);
        if(rect.top <= point[1] && point[1] <= rect.bottom) return true;

        // Top line.
        point = line.getIntersectionPoint(Line3d.Y_COORD, rect.top);
        if(rect.left <= point[0] && point[0] <= rect.right || point[0] == Float.MAX_VALUE) return true;

        // Right line.
        point = line.getIntersectionPoint(Line3d.X_COORD, rect.right);
        if(rect.top <= point[1] && point[1] <= rect.bottom) return true;

        return false;
    }

    /**
     * @return The tangent of a 2d vector.
     */
    public static float vectorAngle(float[] vector){
        return vector[1] / vector[0];
    }

    /**
     * @return The tangent between two vectors. Both 2d and 3d work. The returned angle is in degrees.
     */
    public static float vectorsAngle(float[] vec1, float[] vec2){
        return (float) Math.toDegrees(Math.acos(dotProduct(vec1, vec2) / vectorSize(vec1) / vectorSize(vec2)));
    }

    /**
     * @return The dot product of two vectors. Both 2d and 3d work.
     */
    public static float dotProduct(float[] vec1, float[] vec2){
        int size = max(vec1.length, vec2.length);
        int product = 0;

        for(int i = 0; i < size; i++){
            try{
                product += vec1[i] * vec2[i];
            }catch(Exception e){
                try{
                    product += vec1[i];
                }catch(Exception ex){
                    product += vec2[i];
                }
            }
        }

        return product;
    }

    /**
     * @return The size of a 2d vector.
     */
    public static float vectorSize(float x, float y){
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * @return The size of a vector.
     */
    public static float vectorSize(float[] vector){
        float size = 0;

        for(float x : vector){
            size += Math.pow(x, 2);
        }

        return (float) Math.sqrt(size);
    }

    /**
     * A method that checks whether two entities are side by side.
     * Meaning they can at most have a certain gap between they're collision boxes and have to have they're bottoms at about the same y position.
     * @param e1 The first entity.
     * @param e2 The second entity.
     */
    public static boolean areSideBySide(Unit e1, Unit e2){
        if(!(Math.abs(e1.getCollisionBox().bottom - e2.getCollisionBox().bottom) < 16 * GameView.density())){
            return false;
        }
        if(e1.getCollisionBox().centerX() < e2.getCollisionBox().centerX()){
            if(Math.abs(e1.getCollisionBox().right - e2.getCollisionBox().left) < 8 * GameView.density() + max(e1.getSpeed(), e2.getSpeed()) * GameView.density() &&
                    Math.abs(e1.getCollisionBox().right - e2.getCollisionBox().left) > 0) return true;
        }else{
            if(Math.abs(e2.getCollisionBox().right - e1.getCollisionBox().left) < 8 * GameView.density() + max(e1.getSpeed(), e2.getSpeed()) * GameView.density() &&
                    Math.abs(e2.getCollisionBox().right - e1.getCollisionBox().left) > 0) return true;
        }
        return false;
    }

    /**
     * A method that checks whether two entities are side by side.
     * Meaning they can at most have a certain gap between they're collision boxes and have to have they're bottoms at about the same y position.
     * @param e1 The first entity.
     * @param e2 The second entity.
     */
    public static boolean areSideBySideHeightOfCollisionBox(Unit e1, Unit e2){
        if(!(e1.getCollisionBox().top <= e2.getCollisionBox().centerY() && e1.getCollisionBox().bottom >= e2.getCollisionBox().centerY())){
            return false;
        }
        if(e1.getCollisionBox().centerX() < e2.getCollisionBox().centerX()){
            if(Math.abs(e1.getCollisionBox().right - e2.getCollisionBox().left) < 8 * GameView.density() + max(e1.getSpeed(), e2.getSpeed()) * GameView.density() &&
                    Math.abs(e1.getCollisionBox().right - e2.getCollisionBox().left) > 0) return true;
        }else{
            if(Math.abs(e2.getCollisionBox().right - e1.getCollisionBox().left) < 8 * GameView.density() + max(e1.getSpeed(), e2.getSpeed()) * GameView.density() &&
                    Math.abs(e2.getCollisionBox().right - e1.getCollisionBox().left) > 0) return true;
        }
        return false;
    }

    /**
     *
     * @param array
     * @return The sum of all elements of an int array.
     */
    public static int sumOfArray(int[] array){
        int sum = 0;
        for(int i = 0; i < array.length; i++){
            sum += array[i];
        }
        return sum;
    }

    /**
     * Concatenates two 3 digit integers and returns the string.
     * @param x The integer that comes first.
     * @param y The integer that comes second.
     */
    public static String getString(int x, int y){
        String s1, s2;
        if(x < 10){
            s1 = "00" + String.valueOf(x);
        }else if(x < 100){
            s1 = "0" + String.valueOf(x);
        }else{
            s1 = String.valueOf(x);
        }

        if(y < 10){
            s2 = "00" + String.valueOf(y);
        }else if(y < 100){
            s2 = "0" + String.valueOf(y);
        }else{
            s2 = String.valueOf(y);
        }

        return s1 + s2;
    }

    public static float inGameDistance(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow((y1 - y2) * Consts.DISTANCE_FACTOR, 2));
    }

    /**
     * @return The distance between 2 points in the 3d space.
     */
    public static float distance(float x1, float y1, float z1, float x2, float y2, float z2){
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    /**
     * @return The distance between two points.
     */
    public static float distance(Unit unit, float x2, float y2){
        return distance(unit.getCollisionBox().centerX(), unit.getCollisionBox().centerY(), x2, y2);
    }

    /**
     * @return The distance between two points.
     */
    public static float distance(Unit unit, Unit unit2){
        return distance(unit.getCollisionBox().centerX(), unit.getCollisionBox().centerY(),
                unit2.getCollisionBox().centerX(), unit2.getCollisionBox().centerY());
    }

    /**
     * @return The distance between two points.
     */
    public static float distance(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * @return The index of the maximum element in the int array.
     */
    public static int getIndexOfHighestInArray(int[] colorNumbers) {
        int index = 0;
        int num = 0;
        for(int i = 0; i < colorNumbers.length; i++){
            if(colorNumbers[i] > num){
                num = colorNumbers[i];
                index = i;
            }
        }
        return index;
    }

    /**
     *
     * @param percentage
     * @return The number of pixels that equals to the said percentage of the height of the screen.
     */
    public static float heightPercentage(float percentage) {
        return GameView.getScreenHeight() * percentage / 100;
    }

    /**
     *
     * @param percentage
     * @return The number of pixels that equals to the said percentage of the width of the screen.
     */
    public static float widthPercentage(float percentage) {
        return GameView.getScreenWidth() * percentage / 100;
    }

    /**
     * Returns whether the view contains the point (x, y).
     * @param v The view.
     * @param x
     * @param y
     */
    public static boolean buttonHovered(View v, int x, int y) {
        Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        return rect.contains(x, y);
    }

    /**
     * Returns the percentage value of the width of the screen that equals to the number of pixels given.
     * @param width
     */
    public static int getScreenWidthPercentage(int width) {
        return (int) ((float) width / GameView.getScreenWidth() * 100);
    }

    /**
     * Returns the percentage value of the height of the screen that equals to the number of pixels given.
     * @param height
     */
    public static int getScreenHeightPercentage(int height) {
        return (int) ((float) height / GameView.getScreenHeight() * 100);
    }

    /**
     *
     * @param f1
     * @param f2
     * @return The larger value.
     */
    public static float max(float f1, float f2){
        return (f1 < f2) ? f2 : f1;
    }

    /**
     *
     * @param f1
     * @param f2
     * @return The smaller value.
     */
    public static float min(float f1, float f2){
        return (f1 < f2) ? f1 : f2;
    }

    /**
     *
     * @param i1
     * @param i2
     * @return The larger value.
     */
    public static int max(int i1, int i2){
        return (i1 < i2) ? i2 : i1;
    }

    /**
     *
     * @param i1
     * @param i2
     * @return The smaller value.
     */
    public static int min(int i1, int i2){
        return (i1 < i2) ? i1 : i2;
    }

    /**
     * Checks whether the rectangle and the circle collide.
     * @param rect The rectangle.
     * @param cx The x position of the center of the circle.
     * @param cy The y position of the center of the circle.
     * @param radius The radius of the circle.
     */
    public static boolean rectCollidesCircle(Rect rect, float cx, float cy, float radius) {
        if(rect.contains((int) cx, (int) cy)) return true;
        for(int i = rect.left; i <= rect.right; i += 4 * GameView.density()){
            if(Utils.isInCircle(i, rect.top, cx, cy, radius)) return true;
            if(Utils.isInCircle(i, rect.bottom, cx, cy, radius)) return true;
        }
        for(int i = rect.top; i <= rect.bottom; i += 4 * GameView.density()){
            if(Utils.isInCircle(rect.left, i, cx, cy, radius)) return true;
            if(Utils.isInCircle(rect.right, i, cx, cy, radius)) return true;
        }
        return false;
    }

    public static boolean rectCollidesOval(Rect rect, float cx, float cy, float rx, float ry) {
        if(rect.contains((int) cx, (int) cy)) return true;
        for(int i = rect.left; i <= rect.right; i += 4 * GameView.density()){
            if(Utils.isInOval(i, rect.top, cx, cy, rx, ry)) return true;
            if(Utils.isInOval(i, rect.bottom, cx, cy, rx, ry)) return true;
        }
        for(int i = rect.top; i <= rect.bottom; i += 4 * GameView.density()){
            if(Utils.isInOval(rect.left, i, cx, cy, rx, ry)) return true;
            if(Utils.isInOval(rect.right, i, cx, cy, rx, ry)) return true;
        }
        return false;
    }

    /**
     * Checks whether the rectangle and the circle collide.
     * @param rect The rectangle.
     * @param circle The circle.
     */
    public static boolean rectCollidesCircle(Rect rect, Circle circle) {
        return rectCollidesCircle(rect, circle.getX(), circle.getY(), circle.getRadius());
    }

    /**
     * Checks whether the point (i, j) is in or on the circle (cx, cy, radius).
     */
    public static boolean isInCircle(float i, float j, float cx, float cy, float radius){
        return distance(i, j, cx, cy) <= radius;
    }

    public static boolean isInOval(float i, float j, float x, float y, float rx, float ry){
        return Math.pow((x - i) / rx, 2) + Math.pow((y - j) / ry, 2) <= 1;
    }

    public static boolean inGameIsInCircle(float i, float j, float cx, float cy, float radius){
        return inGameDistance(i, j, cx, cy) <= radius;
    }

    /**
     * Checks whether the point (i, j) is in or on the circle.
     */
    public static boolean isInCircle(float i, float j, Circle circle){
        return distance(i, j, circle.getX(), circle.getY()) <= circle.getRadius();
    }

    /**
     * Quick sort.
     * @param arr The array to be sorted.
     * @param low The element to start sorting from.
     * @param high The last element to include in the sort.
     */
    public static void quickSort(int[] arr, int low, int high) {
        if (arr == null || arr.length == 0)
            return;

        if (low >= high)
            return;

        int middle = low + (high - low) / 2;
        int pivot = arr[middle];

        int i = low, j = high;
        while (i <= j) {
            while (arr[i] < pivot) {
                i++;
            }

            while (arr[j] > pivot) {
                j--;
            }

            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }

        if (low < j)
            quickSort(arr, low, j);

        if (high > i)
            quickSort(arr, i, high);
    }

    /**
     * Draws an oval under a unit's collision box.
     * @param unit The unit.
     * @param canvas The canvas to draw on.
     * @param paint The paint to use for drawing.
     */
    public static void drawOval(Unit unit, Canvas canvas, float xOffset, float yOffset, Paint paint){
        float left = unit.getCollisionBox().left - 4 * GameView.density();
        float right = unit.getCollisionBox().right + 4 * GameView.density();
        float height = unit.getCollisionBox().width() * 2 / 5;
        float top = unit.getCollisionBox().bottom - unit.getCollisionBox().width() / 5;
        float bottom = top + height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawOval(left + xOffset, top + yOffset, right + xOffset, bottom + yOffset, paint);
        }
    }

    /**
     * Draws an oval at a relative position pertinent to a unit.
     * @param unit The unit.
     * @param canvas The canvas to draw on.
     * @param paint The paint to use for drawing.
     */
    public static void drawOval(Unit unit, float x, float y, Canvas canvas, float xOffset, float yOffset, Paint paint){
        float width = unit.getCollisionBox().width();
        float height = width * 4 / 10;
        float top = y - height / 2;
        float bottom = top + height;
        float left = x - width / 2 - 4 * GameView.density();
        float right = left + width + 4 * GameView.density();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawOval(left + xOffset, top + yOffset, right + xOffset, bottom + yOffset, paint);
        }
    }

    public static int getExpNeeded(int level){
        if(level == 0) return 0;
        return 50 * level;
//        return (int) Math.round(1900f / 20 * Math.pow(20f / 19, level));
    }

    public static int getLevelFromExp(int exp){
        int i = 0;

        while(i != Integer.MAX_VALUE){
            if(exp < getExpNeeded(i)){
                return i;
            }
            i++;
        }

        return 0;
    }

    public static float[] crossProduct(float[] vector1, float[] vector2) {
        float[] v1, v2;
        if(vector1.length == 2){
            v1 = new float[] {vector1[0], vector1[1], 0};
        }else{
            v1 = vector1;
        }
        if(vector2.length == 2){
            v2 = new float[] {vector2[0], vector2[1], 0};
        }else{
            v2 = vector2;
        }
        return new float[] {v1[1] * v2[2] - v1[2] * v2[1], v1[2] * v2[0] - v1[0] * v2[2], v1[0] * v2[1] - v1[1] * v2[0]};
    }

    public static Rect translateRect(Rect rect, int dx, int dy) {
        Rect newRect;
        newRect = rect;
        if(rect != null){
            newRect.set(rect.left + dx,
                    rect.top + dy,
                    rect.right + dx,
                    rect.bottom + dy);
        }
        return newRect;
    }

    public static int getRandomIntegerInTheRange(int i, int j, Random random) {
        if(random == null){
            return (int) (Math.random() * (j - i + 1)) + i;
        }else{
            return (int) (random.nextDouble() * (j - i + 1)) + i;
        }
    }

    public static String getCharacterFromInt(int i){
        String character;
        switch(i){
            case 0 :
                character = NameConsts.KNIGHT;
                break;

            case 1 :
                character = NameConsts.PRIEST;
                break;

            case 2 :
                character = NameConsts.ARCHER;
                break;

            default :
                character = "";
        }
        return character;
    }

    public static boolean isCharacter(String carrier) {
        switch(carrier){
            case NameConsts.KNIGHT :
            case NameConsts.PRIEST :
            case NameConsts.ARCHER :
                return true;

            default :
                return false;
        }
    }

    public static boolean isStageLocked(String stage, int levelNumber) {
        if(!FileManager.fileExists(stage + levelNumber + ".tls")) return true;
        if(getLevelValue(stage, levelNumber) <= PlayerStats.getLastLevelUnlocked()) return false;
        return true;
    }

    public static int convertStageNameToInt(String stage){
        switch(stage){
            case Consts.GREEN_VILLAGE :
                return 0;

            case Consts.PASSAGE_TO_THE_FOREST :
                return 1;

            case Consts.FOREST_OF_SHADOWS :
                return 2;

            case Consts.ICE :
                return 3;

            case Consts.SNOW :
                return 4;

            default :
                System.out.println("Can't convert stage name to int.");
                return -33;
        }
    }

    public static int getLevelValue(String stage, int lvl) {
        return Utils.convertStageNameToInt(stage) * 15 + lvl;
    }

    public static int getIntegerFromCharacter(String character) {
        switch(character){
            case NameConsts.KNIGHT :
                return 0;

            case NameConsts.PRIEST :
                return 1;

            case NameConsts.ARCHER :
                return 2;

            case NameConsts.NINJA :
                return 3;

            default :
                System.out.println("Cannot convert the given character to int : " + character);
                return 0;
        }
    }

    public static float convertMsToSeconds(int counter) {
        counter /= 10;
        int radix = counter % 100;
        int seconds = counter / 100;
        return seconds + (float) radix / 100;
    }

    public static void drawScaledBitmap(Bitmap image, float x, float y, float width, float height, Canvas canvas) {
        canvas.drawBitmap(Image.resizeImage(image, width, height), x, y, null);
    }

    public static void drawScaledBitmap(Bitmap image, int x, int y, int width, int height, Canvas canvas) {
        canvas.drawBitmap(Image.resizeImage(image, width, height), x, y, null);
    }

    public static float calculateGainedSpeed(float time, float distance) {
        return (distance / 1000 + time) / 100;
    }

    public static float calculateGainedArmor(float time, float distance) {
        return (float) (distance * Math.sqrt(time) * 10 / 5000000);
    }

    public static String getStringFromItemCategory(String str) {
        switch(str){
            case NameConsts.ITEM_HELMET :
                return GameView.string(R.string.item_helmet);

            case NameConsts.ITEM_WEAPON :
                return GameView.string(R.string.item_weapon);

            case NameConsts.ITEM_ARMOR :
                return GameView.string(R.string.item_armor);

            case NameConsts.ITEM_AMULET :
                return GameView.string(R.string.item_amulet);

            case NameConsts.ITEM_BOOTS :
                return GameView.string(R.string.item_boots);

            default :
                return GameView.string(R.string.all);
        }
    }

    public static Rect getTranslatedRect(Rect rect, float x, float y) {
        return new Rect(rect.left + (int) x, rect.top + (int) y, rect.right + (int) x, rect.bottom + (int) y);
    }

    public static int[] resizeAnimation(int[] images, int width) {
        int[] newImages = new int[images.length];
        System.arraycopy(images, 0, newImages, 0, images.length - 2);
        newImages[images.length - 1] = images[images.length - 1] * width / images[images.length - 2];
        newImages[images.length - 2] = width;
        return newImages;
    }

    public static int[] resizeAnimation(int[] images, float width, float height) {
        int[] newImages = new int[images.length];
        System.arraycopy(images, 0, newImages, 0, images.length - 2);
        newImages[images.length - 2] = (int) width;
        newImages[images.length - 1] = (int) height;
        return newImages;
    }

    public static int[] scaleAnimation(int[] images, float scale) {
        int[] newImages = new int[images.length];
        System.arraycopy(images, 0, newImages, 0, images.length - 2);
        newImages[images.length - 2] = (int) (images[images.length - 2] * scale);
        newImages[images.length - 1] = (int) (images[images.length - 1] * scale);
        return newImages;
    }

    public static float frameInMilliSeconds() {
        return 1000f / GameThread.maxFps / (State.slowMo ? Consts.SLOW_MO_SKIP_FRAMES : 1);
    }

    public static void makeToast(final int stringId){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OpenGL2dActivity.openGL2dActivity, stringId, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static int ceil(float n){
        if(n == (int) n){
            return (int) n;
        }else{
            return (int) n + 1;
        }
    }
}
