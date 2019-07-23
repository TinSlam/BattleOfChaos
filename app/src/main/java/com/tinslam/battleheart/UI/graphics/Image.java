package com.tinslam.battleheart.UI.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;

/**
 * The class that handles operations on images.
 */
public class Image{
    public static Bitmap[] tiles1, tiles2, tiles3, tiles4, tiles5, tiles6, tiles7;

    public static Bitmap priestMoveLeft, priestMoveRight, priestAttackRight, priestAttackLeft;
    public static Bitmap knightMoveRight, knightAttackRight, knightIdleRight;
    public static Bitmap archerMoveLeft, archerMoveRight, archerAttackRight, archerAttackLeft;
    public static Bitmap backgroundArenaForest1, backgroundArenaForest2, backgroundArenaGrass, backgroundArenaIce, backgroundArenaSnow;
    public static Bitmap backgroundPortalGreen, backgroundPortalForest, backgroundPortalDarkForest, backgroundPortalIce, backgroundPortalSnow;
    public static Bitmap greenGoblinMoveLeft, greenGoblinMoveRight, greenGoblinAttackRight, greenGoblinAttackLeft;
    public static Bitmap trollAttackLeft, trollAttackRight, trollMoveLeft, trollMoveRight;
    public static Bitmap mummyArcherMoveLeft, mummyArcherMoveRight, mummyArcherAttackLeft, mummyArcherAttackRight;
    public static Bitmap visualEffectHeal, visualEffectTaunt;

    public static Bitmap villager1Left;

    public static Bitmap unknown;

    public static Bitmap level_empty, level_empty_locked;
    public static Bitmap button_empty, button_empty_hover;
    public static Bitmap color_blue, color_yellow, blue_arrow_right;
    public static Bitmap brown_panel, light_brown_panel, yellow_panel;
    public static Bitmap quest_panel, quest_name_box, quest_more_button;

    public static Bitmap portrait_taunt, portrait_heal_aoe;

    public static Bitmap arrow;

    public static Bitmap item_wooden_bow, item_magic_wand, item_giant_shield, item_simple_shield, item_light_threads;
    public static Bitmap item_sultans_dagger;

    public static Bitmap gold, hourglass, footprints;

    public static Bitmap bounty, damageVisualEffect;

    public static Bitmap sun_strike, sun_strike_visual_indicator;

    public static Bitmap grass_tile;

    public static float knightImageScale = 1f / 2, archerImageScale = 5f / 9, priestImageScale = 5f / 4;
    public static float mummyArcherImageScale = 1f;

    public static Bitmap rain_1, rain_2;

    public static Bitmap teleport;

    /**
     * Loads the images needed for the program. Is called at the start of the game.
     */
    public static void loadImages(){
        Resources res = MyGL2dRenderer.getContext().getResources();

        Bitmap tileset1 = BitmapFactory.decodeResource(res, R.drawable.tiles1);
        Bitmap tileset2 = BitmapFactory.decodeResource(res, R.drawable.tiles2);
        Bitmap tileset3 = BitmapFactory.decodeResource(res, R.drawable.tiles3);
        Bitmap tileset4 = BitmapFactory.decodeResource(res, R.drawable.tiles4);
        Bitmap tileset5 = BitmapFactory.decodeResource(res, R.drawable.tiles5);
        Bitmap tileset6 = BitmapFactory.decodeResource(res, R.drawable.tiles6);
        Bitmap tileset7 = BitmapFactory.decodeResource(res, R.drawable.tiles7);
        tiles1 = new Bitmap[17 * 8];
        processTileset(tiles1, tileset1, 8, 17);
        tiles2 = new Bitmap[25 * 8];
        processTileset(tiles2, tileset2, 8, 25);
        tiles3 = new Bitmap[33 * 8];
        processTileset(tiles3, tileset3, 8, 33);
        tiles4 = new Bitmap[18 * 8];
        processTileset(tiles4, tileset4, 8, 18);
        tiles5 = new Bitmap[19 * 8];
        processTileset(tiles5, tileset5, 8, 19);
        tiles6 = new Bitmap[28 * 8];
        processTileset(tiles6, tileset6, 8, 28);
        tiles7 = new Bitmap[33 * 8];
        processTileset(tiles7, tileset7, 8, 33);

        color_blue = BitmapFactory.decodeResource(res, R.drawable.color_blue);
        color_yellow = BitmapFactory.decodeResource(res, R.drawable.color_yellow);
        blue_arrow_right = BitmapFactory.decodeResource(res, R.drawable.blue_arrow_right);

        portrait_taunt = BitmapFactory.decodeResource(res, R.drawable.button_empty);
        portrait_heal_aoe = BitmapFactory.decodeResource(res, R.drawable.button_empty_hover);

        button_empty = BitmapFactory.decodeResource(res, R.drawable.button_empty);
        button_empty_hover = BitmapFactory.decodeResource(res, R.drawable.button_empty_hover);
        brown_panel = BitmapFactory.decodeResource(res, R.drawable.brown_panel);
        light_brown_panel = BitmapFactory.decodeResource(res, R.drawable.light_brown_panel);
        yellow_panel = BitmapFactory.decodeResource(res, R.drawable.yellow_panel);
        level_empty = BitmapFactory.decodeResource(res, R.drawable.level_empty);
        level_empty_locked = BitmapFactory.decodeResource(res, R.drawable.level_empty_locked);
        quest_panel = BitmapFactory.decodeResource(res, R.drawable.quest_panel);
        quest_name_box = BitmapFactory.decodeResource(res, R.drawable.quest_name_box);
        quest_more_button = BitmapFactory.decodeResource(res, R.drawable.quest_more_button);

        visualEffectHeal = BitmapFactory.decodeResource(res, R.drawable.visual_effect_heal);
        visualEffectTaunt = BitmapFactory.decodeResource(res, R.drawable.visual_effect_taunt);

        rain_1 = BitmapFactory.decodeResource(res, R.drawable.rain_1);
        rain_2 = BitmapFactory.decodeResource(res, R.drawable.rain_2);

        priestMoveRight = BitmapFactory.decodeResource(res, R.drawable.priest_move_right);
        priestMoveLeft = BitmapFactory.decodeResource(res, R.drawable.priest_move_left);
        priestAttackRight = BitmapFactory.decodeResource(res, R.drawable.priest_attack_right);
        priestAttackLeft = BitmapFactory.decodeResource(res, R.drawable.priest_attack_left);
        priestMoveRight = resizeImage(priestMoveRight, priestMoveRight.getWidth() * priestImageScale);
        priestMoveLeft = resizeImage(priestMoveLeft, priestMoveLeft.getWidth() * priestImageScale);
        priestAttackLeft = resizeImage(priestAttackLeft, priestAttackLeft.getWidth() * priestImageScale);
        priestAttackRight = resizeImage(priestAttackRight, priestAttackRight.getWidth() * priestImageScale);

        archerMoveRight = BitmapFactory.decodeResource(res, R.drawable.archer_move_right);
        archerMoveLeft = BitmapFactory.decodeResource(res, R.drawable.archer_move_left);
        archerAttackRight = BitmapFactory.decodeResource(res, R.drawable.archer_move_right);
        archerAttackLeft = BitmapFactory.decodeResource(res, R.drawable.archer_move_left);
        archerMoveRight = resizeImage(archerMoveRight, archerMoveRight.getWidth() * archerImageScale);
        archerMoveLeft = resizeImage(archerMoveLeft, archerMoveLeft.getWidth() * archerImageScale);
        archerAttackLeft = resizeImage(archerAttackLeft, archerAttackLeft.getWidth() * archerImageScale);
        archerAttackRight = resizeImage(archerAttackRight, archerAttackRight.getWidth() * archerImageScale);

        knightMoveRight = BitmapFactory.decodeResource(res, R.drawable.knight_move_right_new);
        knightIdleRight = BitmapFactory.decodeResource(res, R.drawable.knight_idle_right);
        knightAttackRight = BitmapFactory.decodeResource(res, R.drawable.knight_attack_right_new);
        knightMoveRight = resizeImage(knightMoveRight, knightMoveRight.getWidth() * knightImageScale);
        knightIdleRight = resizeImage(knightIdleRight, knightIdleRight.getWidth() * knightImageScale);
        knightAttackRight = resizeImage(knightAttackRight, knightAttackRight.getWidth() * knightImageScale);

        mummyArcherMoveRight = BitmapFactory.decodeResource(res, R.drawable.mummy_archer_move_right);
        mummyArcherMoveLeft = BitmapFactory.decodeResource(res, R.drawable.mummy_archer_move_left);
        mummyArcherAttackRight = BitmapFactory.decodeResource(res, R.drawable.mummy_archer_move_right);
        mummyArcherAttackLeft = BitmapFactory.decodeResource(res, R.drawable.mummy_archer_move_left);
        mummyArcherMoveRight = resizeImage(mummyArcherMoveRight, mummyArcherMoveRight.getWidth() * mummyArcherImageScale);
        mummyArcherMoveLeft = resizeImage(mummyArcherMoveLeft, mummyArcherMoveLeft.getWidth() * mummyArcherImageScale);
        mummyArcherAttackLeft = resizeImage(mummyArcherAttackLeft, mummyArcherAttackLeft.getWidth() * mummyArcherImageScale);
        mummyArcherAttackRight = resizeImage(mummyArcherAttackRight, mummyArcherAttackRight.getWidth() * mummyArcherImageScale);

        greenGoblinMoveLeft = BitmapFactory.decodeResource(res, R.drawable.goblin_green_move_left);
        greenGoblinMoveRight = BitmapFactory.decodeResource(res, R.drawable.goblin_green_move_right);
        greenGoblinAttackLeft = BitmapFactory.decodeResource(res, R.drawable.goblin_green_attack_left);
        greenGoblinAttackRight = BitmapFactory.decodeResource(res, R.drawable.goblin_green_attack_right);

        backgroundArenaForest1 = BitmapFactory.decodeResource(res, R.drawable.background_arena_forest1);
        backgroundArenaForest2 = BitmapFactory.decodeResource(res, R.drawable.background_arena_forest2);
        backgroundArenaGrass = BitmapFactory.decodeResource(res, R.drawable.background_arena_grass);
        backgroundArenaIce = BitmapFactory.decodeResource(res, R.drawable.background_arena_ice);
        backgroundArenaSnow = BitmapFactory.decodeResource(res, R.drawable.background_arena_snow);

        backgroundPortalGreen = BitmapFactory.decodeResource(res, R.drawable.background_portal_green);
        backgroundPortalForest = BitmapFactory.decodeResource(res, R.drawable.background_portal_light_forest);
        backgroundPortalDarkForest = BitmapFactory.decodeResource(res, R.drawable.background_portal_dark_forest);
        backgroundPortalIce = BitmapFactory.decodeResource(res, R.drawable.background_portal_ice);
        backgroundPortalSnow = BitmapFactory.decodeResource(res, R.drawable.background_portal_snow);

        trollAttackRight = BitmapFactory.decodeResource(res, R.drawable.troll_attack_right);
        trollAttackRight = resizeImage(trollAttackRight, trollAttackRight.getWidth() * 2, trollAttackRight.getHeight() * 2);
        trollAttackLeft = BitmapFactory.decodeResource(res, R.drawable.troll_attack_left);
        trollAttackLeft = resizeImage(trollAttackLeft, trollAttackLeft.getWidth() * 2, trollAttackLeft.getHeight() * 2);
        trollMoveLeft = BitmapFactory.decodeResource(res, R.drawable.troll_move_left);
        trollMoveLeft = resizeImage(trollMoveLeft, trollMoveLeft.getWidth() * 2, trollMoveLeft.getHeight() * 2);
        trollMoveRight = BitmapFactory.decodeResource(res, R.drawable.troll_move_right);
        trollMoveRight = resizeImage(trollMoveRight, trollMoveRight.getWidth() * 2, trollMoveRight.getHeight() * 2);

        arrow = BitmapFactory.decodeResource(res, R.drawable.arrow);

        gold = BitmapFactory.decodeResource(res, R.drawable.gold);
        hourglass = BitmapFactory.decodeResource(res, R.drawable.hourglass);
        footprints = BitmapFactory.decodeResource(res, R.drawable.footprints);

        unknown = BitmapFactory.decodeResource(res, R.drawable.black_square);

        bounty = BitmapFactory.decodeResource(res, R.drawable.gold);
        damageVisualEffect = BitmapFactory.decodeResource(res, R.drawable.damage_visual_effect);
        bounty = resizeImage(bounty, GameView.density() * 12);
        damageVisualEffect = resizeImage(damageVisualEffect, GameView.density() * 12);

        item_giant_shield = BitmapFactory.decodeResource(res, R.drawable.giant_shield);
        item_light_threads = BitmapFactory.decodeResource(res, R.drawable.light_threads);
        item_magic_wand = BitmapFactory.decodeResource(res, R.drawable.magic_wand);
        item_simple_shield = BitmapFactory.decodeResource(res, R.drawable.simple_shield);
        item_wooden_bow = BitmapFactory.decodeResource(res, R.drawable.wooden_bow);
        item_sultans_dagger = BitmapFactory.decodeResource(res, R.drawable.sultans_dagger);

        sun_strike = BitmapFactory.decodeResource(res, R.drawable.sun_strike);
        sun_strike_visual_indicator = BitmapFactory.decodeResource(res, R.drawable.sun_strike_visual_indicator);

        grass_tile = BitmapFactory.decodeResource(res, R.drawable.grass_tile);

        villager1Left = BitmapFactory.decodeResource(res, R.drawable.villager_1_left);

        teleport = BitmapFactory.decodeResource(res, R.drawable.teleport);
    }

    private static void processTileset(Bitmap[] tileset, Bitmap spreadsheet, int width, int height){
        int tileWidth = spreadsheet.getWidth() / width;
        int tileHeight = spreadsheet.getHeight() / height;
        for(int i = 0; i < tileset.length; i++){
            tileset[i] = Bitmap.createBitmap(spreadsheet, tileWidth * (i % width), tileHeight * (i / width),
                    tileWidth, tileHeight);
            tileset[i] = resizeImage(tileset[i], 32 * GameView.density(), 32 * GameView.density());
        }
    }

    /**
     * Replaces the black pixels of the image with the given color.
     * @return The edited image.
     */
    public static Bitmap replaceBlackWithColor(Bitmap srcBmp, int dstColor){
        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();

        Bitmap dstBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if(((srcBmp.getPixel(col, row) & 0xff000000) >> 24) == 0){
                    dstBitmap.setPixel(col, row, dstColor);
                }else{
                    dstBitmap.setPixel(col, row, srcBmp.getPixel(col, row));
                }
            }
        }

        return dstBitmap;
    }

    /**
     * Gives the bitmap a new color filter ? Idk something like that.
     * @return The edited image.
     */
    public static Bitmap changeImageColor(Bitmap srcBmp, int dstColor) {
        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();

        float srcHSV[] = new float[3];
        float dstHSV[] = new float[3];

        Bitmap dstBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if(((srcBmp.getPixel(col, row) & 0xff000000) >> 24) == 0) continue;
                Color.colorToHSV(srcBmp.getPixel(col, row), srcHSV);
                Color.colorToHSV(dstColor, dstHSV);

                // If it area to be painted set only value of original image
                dstHSV[2] = srcHSV[2];  // value

                dstBitmap.setPixel(col, row, Color.HSVToColor(dstHSV));
            }
        }

        return dstBitmap;
    }

    /**
     * Draws a circle with the given color on a bitmap. This may not be an accurate explanation. Dude idk what I'm doing.
     */
    public static Bitmap coverCircleWithColor(Bitmap bitmap, int color){
        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap image = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(127);
        canvas.drawCircle(image.getWidth() / 2, image.getHeight() / 2, image.getWidth() / 2, paint);
        return image;
    }

    public static Bitmap resizeImage(int id, float newWidth, float newHeight){
        Bitmap image = BitmapFactory.decodeResource(GameView.Context().getResources(), id);
        return resizeImage(image, newWidth, newHeight);
    }

    /**
     * Resizes the Bitmap.
     * @param bm The original Bitmap.
     * @param newWidth The new width.
     * @param newHeight The new height.
     * @return The new Bitmap.
     */
    public static Bitmap resizeImage(Bitmap bm, float newWidth, float newHeight) {
        return resizeImage(bm, (int) newWidth, (int) newHeight);
    }

    /**
     * Resizes the Bitmap.
     * @param bm The original Bitmap.
     * @param newWidth The new width.
     * @param newHeight The new height.
     * @return The new Bitmap.
     */
    public static Bitmap resizeImage(Bitmap bm, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(
                bm, newWidth, newHeight, false);
    }

    /**
     * Flips the bitmap horizontally.
     */
    public static Bitmap flipHorizontally(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Flips the bitmap horizontally.
     */
    public static Bitmap[] flipHorizontally(Bitmap[] bitmaps) {
        Bitmap[] newBitmaps = new Bitmap[bitmaps.length];
        for(int i = 0; i < bitmaps.length; i++){
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1, bitmaps[i].getWidth() / 2, bitmaps[i].getHeight() / 2);
            newBitmaps[i] = Bitmap.createBitmap(bitmaps[i], 0, 0, bitmaps[i].getWidth(), bitmaps[i].getHeight(), matrix, true);
        }
        return newBitmaps;
    }

    /**
     * Resizes the Bitmap. Keeping the ratio.
     * @param bm The original Bitmap.
     * @param newWidth The new width.
     * @return The new Bitmap.
     */
    public static Bitmap resizeImage(Bitmap bm, float newWidth) {
        return resizeImage(bm, (int) newWidth);
    }

    /**
     * Resizes the Bitmap. Keeping the ratio.
     * @param bm The original Bitmap.
     * @param newWidth The new width.
     * @return The new Bitmap.
     */
    public static Bitmap resizeImage(Bitmap bm, int newWidth) {
        int newHeight = (int) (bm.getHeight() * (float) newWidth / bm.getWidth());
        return Bitmap.createScaledBitmap(
                bm, newWidth, newHeight, false);
    }

    /**
     * Resizes all the images in the Bitmap array. Keeping the ratio.
     * @param images The array.
     * @param newWidth The width per image.
     */
    public static Bitmap[] resizeImages(Bitmap[] images, float newWidth){
        try{
            Bitmap[] newImages = new Bitmap[images.length];
            for(int i = 0; i < images.length; i++){
                newImages[i] = resizeImage(images[i], newWidth);
            }
            return newImages;
        }catch(Exception e){
            return images;
        }
    }

    /**
     * Resizes all the images in the Bitmap array.
     * @param images The array.
     * @param newWidth The width per image.
     * @param newHeight The height per image.
     */
    public static Bitmap[] resizeImages(Bitmap[] images, float newWidth, float newHeight){
        Bitmap[] newImages = new Bitmap[images.length];
        for(int i = 0; i < images.length; i++){
            newImages[i] = resizeImage(images[i], newWidth, newHeight);
        }
        return newImages;
    }

    public static Bitmap circlify(Bitmap bitmap){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public static Bitmap getBitmap(int id) {
        return BitmapFactory.decodeResource(GameView.Context().getResources(), id);
    }
}
