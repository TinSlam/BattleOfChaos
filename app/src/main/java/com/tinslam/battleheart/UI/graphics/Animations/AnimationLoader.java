package com.tinslam.battleheart.UI.graphics.Animations;

import android.graphics.Bitmap;

import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.utils.utils3D.TextureHelper;

/**
 * The class that handles loading animations.
 */
public class AnimationLoader{
    public static int[] knightMoveRight, knightMoveLeft, knightAttackLeft, knightAttackRight, knightIdleLeft, knightIdleRight;
    public static int[] priestMoveRight, priestMoveLeft, priestAttackLeft, priestAttackRight;
    public static int[] archerMoveRight, archerMoveLeft, archerAttackLeft, archerAttackRight;
    public static int[] greenGoblinMoveRight, greenGoblinMoveLeft, greenGoblinAttackLeft, greenGoblinAttackRight;
    public static int[] trollMoveRight, trollMoveLeft, trollAttackRight, trollAttackLeft;
    public static int[] mummyArcherMoveRight, mummyArcherMoveLeft, mummyArcherAttackRight, mummyArcherAttackLeft;
    public static int[] visualEffectHeal, visualEffectTaunt;
    public static int[] villager1Left;

    public static int[] arrow;

    public static int[] bountyVisualEffect, damageVisualEffect;

    public static int[] sunStrike;

    public static int[] teleport;

    public static int[] rain_1, rain_2;

    private static boolean first = true;

    /**
     * Loads all the animation used in the program. Is called at the start of the game.
     */
    public static void loadAnimations(){
        visualEffectHeal = processImage(Image.visualEffectHeal, 6);
        visualEffectTaunt = processImage(Image.visualEffectTaunt, 7);

//        knightMoveRight = Animation.processImage(Image.knightMoveRight, 8);
//        knightAttackLeft = Animation.processImage(Image.knightAttackLeft, 6);
        knightIdleRight = processImage(Image.knightIdleRight, 10);
        knightMoveRight = processImage(Image.knightMoveRight, 10);
        knightAttackRight = processImage(Image.knightAttackRight, 10);
//        knightAttackRight = new Bitmap[] {Image.getBitmap(R.drawable.knight_attack_1),
//                Image.getBitmap(R.drawable.knight_attack_2),
//                Image.getBitmap(R.drawable.knight_attack_3),
//                Image.getBitmap(R.drawable.knight_attack_4),
//                Image.getBitmap(R.drawable.knight_attack_5),
//                Image.getBitmap(R.drawable.knight_attack_6),
//                Image.getBitmap(R.drawable.knight_attack_7),
//                Image.getBitmap(R.drawable.knight_attack_8),
//                Image.getBitmap(R.drawable.knight_attack_9),
//                Image.getBitmap(R.drawable.knight_attack_10)};
//        knightAttackRight = Image.resizeImages(knightAttackRight, Image.knightAttackRight.getWidth() / 6, Image.knightAttackRight.getHeight());
//        knightMoveLeft = Animation.processImage(Image.knightMoveLeft, 8);
        knightAttackLeft = processImage(Image.flipHorizontally(Image.knightAttackRight), 10);
        knightMoveLeft = processImage(Image.flipHorizontally(Image.knightMoveRight), 10);
        knightIdleLeft = processImage(Image.flipHorizontally(Image.knightIdleRight), 10);

        priestMoveRight = processImage(Image.priestMoveRight, 1);
        priestAttackLeft = processImage(Image.priestAttackLeft, 8);
        priestAttackRight = processImage(Image.priestAttackRight, 8);
        priestMoveLeft = processImage(Image.priestMoveLeft, 1);

        archerMoveRight = processImage(Image.archerMoveRight, 6);
        archerAttackLeft = processImage(Image.archerAttackLeft, 6);
        archerAttackRight = processImage(Image.archerAttackRight, 6);
        archerMoveLeft = processImage(Image.archerMoveLeft, 6);

        greenGoblinAttackLeft = processImage(Image.greenGoblinAttackLeft, 4);
        greenGoblinAttackRight = processImage(Image.greenGoblinAttackRight, 4);
        greenGoblinMoveLeft = processImage(Image.greenGoblinMoveLeft, 3);
        greenGoblinMoveRight = processImage(Image.greenGoblinMoveRight, 3);

        trollAttackLeft = processImage(Image.trollAttackLeft, 4);
        trollAttackRight = processImage(Image.trollAttackRight, 4);
        trollMoveRight = processImage(Image.trollMoveRight, 5);
        trollMoveLeft = processImage(Image.trollMoveLeft, 5);

        mummyArcherAttackLeft = processImage(Image.mummyArcherAttackLeft, 5);
        mummyArcherAttackRight = processImage(Image.mummyArcherAttackRight, 5);
        mummyArcherMoveRight = processImage(Image.mummyArcherMoveRight, 5);
        mummyArcherMoveLeft = processImage(Image.mummyArcherMoveLeft, 5);

        arrow = processImage(Image.arrow, 1);

        bountyVisualEffect = processImage(Image.bounty, 1);
        damageVisualEffect = processImage(Image.damageVisualEffect, 1);

        sunStrike = processImage(Image.sun_strike, 7);

        teleport = processImage(Image.teleport, 6);

        rain_1 = processImage(Image.rain_1, 3);
        rain_2 = processImage(Image.rain_2, 8);

        villager1Left = processImage(Image.villager1Left, 1);

        if(first){
            first = false;
            PlayerStats.loadPortraits();
        }
    }

    static int[] processImage(Bitmap rawImage, int size){
        int[] images = new int[size + 2];
        int width = rawImage.getWidth() / size;
        for(int i = 0; i < size; i++){
            images[i] = TextureHelper.loadTexture(Bitmap.createBitmap(rawImage, i * width, 0, width, rawImage.getHeight()), images[i]);
        }
        images[size] = width;
        images[size + 1] = rawImage.getHeight();
        return images;
    }
}
