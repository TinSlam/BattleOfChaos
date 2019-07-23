package com.tinslam.battleheart.utils.constants;

public class UnitConsts{
    // Knight.
    public static final float KNIGHT_HP = 160,
            KNIGHT_DAMAGE = 15,
            KNIGHT_SPEED = 4,
            KNIGHT_ATTACK_RANGE = -1,
            KNIGHT_ARMOR = 4;
    public static final int KNIGHT_AGGRO_RANGE = 200,
            KNIGHT_ATTACK_CD = 1000;
    public static final float KNIGHT_HP_GROWTH = 8,
            KNIGHT_ARMOR_GROWTH = (float) 0.3,
            KNIGHT_DAMAGE_GROWTH = (float) 2.2;

    // Priest.
    public static final float PRIEST_HP = 100,
            PRIEST_DAMAGE = -10,
            PRIEST_SPEED = 3.5f,
            PRIEST_ATTACK_RANGE = 200,
            PRIEST_ARMOR = 1;
    public static final int PRIEST_AGGRO_RANGE = 200,
            PRIEST_ATTACK_CD = 1000;
    public static final float PRIEST_HP_GROWTH = 4,
            PRIEST_ARMOR_GROWTH = (float) 0.1,
            PRIEST_DAMAGE_GROWTH = (float) -1.9;

    // Archer.
    public static final float ARCHER_HP = 120,
            ARCHER_DAMAGE = 20,
            ARCHER_SPEED = 5,
            ARCHER_ATTACK_RANGE = 200,
            ARCHER_ARMOR = 2.1f;
    public static final int ARCHER_AGGRO_RANGE = 350,
            ARCHER_ATTACK_CD = 1000;
    public static final float ARCHER_HP_GROWTH = 6.2f,
            ARCHER_ARMOR_GROWTH = 0.2f,
            ARCHER_DAMAGE_GROWTH = 2.8f;
    public static final float ARCHER_PROJECTILE_SPEED = 30;
}
