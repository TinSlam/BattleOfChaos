package com.tinslam.battleheart.utils.constants;

import com.tinslam.battleheart.base.GameView;

/**
 * The class that contains all the constant variables.
 */
public class Consts{
    public static final float DISTANCE_FACTOR = 2;

    public static final byte INPUT_COMMAND_DRAG = 0,
    INPUT_COMMAND_POINT = 1;

    public static final String GREEN_VILLAGE = "Green Village",
    PASSAGE_TO_THE_FOREST = "Passage To The Forest",
    FOREST_OF_SHADOWS = "Forest Of Shadows",
    ICE = "Ice",
    SNOW = "Snow";

    public static final float MINIMUM_SPEED = 0.5f,
    MAXIMUM_SPEED = 12;

    public static final int SLOW_MO_SKIP_FRAMES = 2;

    public static final int PORTRAIT_WIDTH = GameView.getScreenWidth() / 7,
    PORTRAIT_HEIGHT = GameView.getScreenHeight() / 4;

    public static final float CAMERA_EYE_X = 32,
    CAMERA_EYE_Y = 32,
    CAMERA_EYE_Z = -16,
    CAMERA_LOOK_X = 0,
    CAMERA_LOOK_Y = 0,
    CAMERA_LOOK_Z = -1,
    CAMERA_ANGLE = 45,
    CAMERA_UP_X = 0,
    CAMERA_UP_Y = 0,
    CAMERA_UP_Z = -1;

    public static final byte STATE_HOLD = 0,
    STATE_MOVE = 1,
    STATE_ATTACK = 2,
    STATE_CAST = 3;

    public static final byte TEAM_ALLIED = 0,
    TEAM_ENEMY = 1,
    TEAM_NEUTRAL = 2;

    public static final int MAX_CHARS_USERNAME = 25,
    MAX_CHARS_PASSWORD = 255,
    MAX_CHARS_EMAIL = 255,
    MIN_CHARS_PASSWORD = 7,
    MIN_CHARS_USERNAME = 3;

    public static final int CAMERA_MOVE_TIME = 1000;

    public static final int TIME_AUTO_WAVE_SPAWN = 60 * 1000,
    TIME_WAVE_SPAWN_DELAY = 3 * 1000;

    public static final float GL_CAMERA_SPEED = 30,
    GL_CAMERA_ZOOM_SPEED = 30;
}