package com.tinslam.battleheart.utils.constants;

import com.tinslam.battleheart.base.GameView;

public class SpellConsts{
    public static final int PORTRAIT_WIDTH = GameView.getScreenHeight() / 5,
    PORTRAIT_HEIGHT = GameView.getScreenHeight() / 5,
    SLOW_MO_TIME = (int) (1500 / Math.pow(Consts.SLOW_MO_SKIP_FRAMES, 3));

    public static final int TAUNT_TIME = 5000;
    public static final float TAUNT_BONUS_MS = 2,
    TAUNT_RADIUS = 400,
    TAUNT_LVL_REQUIRED = 5;

    public static final float HEAL_AOE_AMOUNT = 20,
    HEAL_AOE_LVL_REQUIRED = 5;

    public static final float POWERSHOT_SPEED = 50,
    POWERSHOT_DAMAGE = 30,
    POWERSHOT_LVL_REQUIRED = 5;

    public static final float SUN_STRIKE_DAMAGE = 40;
}