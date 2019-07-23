package com.tinslam.battleheart.elements3D;

import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.utils.utils3D.TextureHelper;
import com.tinslam.battleheart.R;

/**
 * A class that holds all the textures.
 */
public class TextureData{
    public static int greenBox, grass, castle, crate, orangeBox, stall, color_blue_3d, color_yellow_3d;

    public static int button_empty, button_empty_hover;

    public static int sun_strike_visual_indicator;
    public static int portrait_heal_aoe, portrait_taunt, portrait_powershot;
    public static int blue_arrow_right;
    public static int unknown, transparent_black_square;
    public static int quest_more_button;
    public static int level_empty_locked, level_empty;
    public static int color_blue, color_yellow;
    public static int backgroundArenaForest1;
    public static int confirmation_panel;
    public static int light_brown_panel;
    public static int brown_panel;
    public static int yellow_panel;
    public static int backgroundPortalForest;
    public static int backgroundPortalDarkForest;
    public static int backgroundPortalIce;
    public static int backgroundPortalSnow;
    public static int backgroundPortalGreen;
    public static int sign;
    public static int gold;
    public static int hourglass;
    public static int backgroundArenaForest2;
    public static int backgroundArenaIce;
    public static int backgroundArenaSnow;
    public static int backgroundArenaGrass;
    public static int solid_green, solid_yellow, solid_black, solid_red, solid_blue, solid_dark_blue, solid_white;
    public static int selected_character_visual, selected_item_visual;
    public static int item_giant_shield;
    public static int item_simple_shield;
    public static int item_light_threads;
    public static int item_magic_wand;
    public static int item_sultans_dagger;
    public static int item_wooden_bow;
    public static int quest_panel;
    public static int quest_name_box;
    public static int footprints;
    public static int round_rect_solid_black;
    public static int round_rect_solid_green;
    public static int solid_cyan;
    public static int outlined_black_square;
    public static int grass_tile;
    public static int[] tiles1, tiles2, tiles3, tiles4, tiles5, tiles6, tiles7;

    public static int atlas_training, atlas_0;

    public static int giantRock1, giantRock2, giantRock3, giantAxe;

    /**
     * Loads the textures.
     */
    public static void loadTextures(){
        greenBox = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.cube, greenBox);
        stall = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.stall_texture, stall);
        grass = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.noisy_grass_public_domain, grass);
        castle = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.stone_wall_public_domain, castle);
        crate = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.crate_1, crate);
        orangeBox = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.level_empty, orangeBox);
        color_blue_3d = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.color_blue, color_blue_3d);
        color_yellow_3d = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.color_yellow, color_yellow_3d);
    }

    private static boolean first = true;

    public static void load2dTextures(){
        if(first){
            first = false;
            Image.loadImages();
        }
        tiles1 = new int[Image.tiles1.length];
        for(int i = 0; i < tiles1.length; i++){
            tiles1[i] = TextureHelper.loadTexture(Image.tiles1[i], tiles1[i]);
        }
        tiles2 = new int[Image.tiles2.length];
        for(int i = 0; i < tiles2.length; i++){
            tiles2[i] = TextureHelper.loadTexture(Image.tiles2[i], tiles2[i]);
        }
        tiles3 = new int[Image.tiles3.length];
        for(int i = 0; i < tiles3.length; i++){
            tiles3[i] = TextureHelper.loadTexture(Image.tiles3[i], tiles3[i]);
        }
        tiles4 = new int[Image.tiles4.length];
        for(int i = 0; i < tiles4.length; i++){
            tiles4[i] = TextureHelper.loadTexture(Image.tiles4[i], tiles4[i]);
        }
        tiles5 = new int[Image.tiles5.length];
        for(int i = 0; i < tiles5.length; i++){
            tiles5[i] = TextureHelper.loadTexture(Image.tiles5[i], tiles5[i]);
        }
        tiles6 = new int[Image.tiles6.length];
        for(int i = 0; i < tiles6.length; i++){
            tiles6[i] = TextureHelper.loadTexture(Image.tiles6[i], tiles6[i]);
        }
        tiles7 = new int[Image.tiles7.length];
        for(int i = 0; i < tiles7.length; i++){
            tiles7[i] = TextureHelper.loadTexture(Image.tiles7[i], tiles7[i]);
        }

//        greenBox = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.cube, greenBox);
//        stall = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.stall_texture, stall);
//        grass = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.noisy_grass_public_domain, grass);
//        castle = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.stone_wall_public_domain, castle);
//        crate = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.crate_1, crate);
//        orangeBox = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.level_empty, orangeBox);
        button_empty = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.button_empty, button_empty);
        button_empty_hover = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.button_empty_hover, button_empty_hover);
        portrait_heal_aoe = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.button_empty_hover, portrait_heal_aoe);
        portrait_taunt = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.button_empty, portrait_taunt);
        portrait_powershot = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.button_empty, portrait_powershot);
        blue_arrow_right = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.blue_arrow_right, blue_arrow_right);
        unknown = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.black_square, unknown);
        quest_more_button = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.quest_more_button, quest_more_button);
        level_empty_locked = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.level_empty_locked, level_empty_locked);
        level_empty = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.level_empty, level_empty);
        color_blue = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.color_blue, color_blue);
        color_yellow = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.color_yellow, color_yellow);
        backgroundArenaForest1 = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_arena_forest1, backgroundArenaForest1);
        transparent_black_square = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.transparent_black_square, transparent_black_square);
        confirmation_panel = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.confirmation_panel, confirmation_panel);
        light_brown_panel = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.light_brown_panel, light_brown_panel);
        yellow_panel = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.yellow_panel, yellow_panel);
        brown_panel = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.brown_panel, brown_panel);
        backgroundPortalDarkForest = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_portal_dark_forest, backgroundPortalDarkForest);
        backgroundPortalForest = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_portal_light_forest, backgroundPortalForest);
        backgroundPortalGreen = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_portal_green, backgroundPortalGreen);
        backgroundPortalIce = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_portal_ice, backgroundPortalIce);
        backgroundPortalSnow = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_arena_snow, backgroundPortalSnow);
        sign = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.sign, sign);
        gold = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.gold, gold);
        hourglass = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.hourglass, hourglass);
        backgroundArenaForest2 = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_arena_forest2, backgroundArenaForest2);
        backgroundArenaGrass = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_arena_grass, backgroundArenaGrass);
        backgroundArenaIce = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_arena_ice, backgroundArenaIce);
        backgroundArenaSnow = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.background_arena_snow, backgroundArenaSnow);
        solid_yellow = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.solid_yellow, solid_yellow);
        solid_blue = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.solid_blue, solid_blue);
        solid_dark_blue = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.solid_dark_blue, solid_dark_blue);
        solid_white = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.solid_white, solid_white);
        solid_red = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.solid_red, solid_red);
        solid_green = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.solid_green, solid_green);
        solid_black = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.solid_black, solid_black);
        selected_character_visual = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.selected_character_visual, selected_character_visual);
        selected_item_visual = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.selected_item_visual, selected_item_visual);
        item_giant_shield = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.giant_shield, item_giant_shield);
        item_light_threads = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.light_threads, item_light_threads);
        item_magic_wand = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.magic_wand, item_magic_wand);
        item_simple_shield = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.simple_shield, item_simple_shield);
        item_sultans_dagger = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.sultans_dagger, item_sultans_dagger);
        item_wooden_bow = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.wooden_bow, item_wooden_bow);
        quest_panel = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.quest_panel, quest_panel);
        quest_name_box = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.quest_name_box, quest_name_box);
        footprints = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.footprints, footprints);
        solid_cyan = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.solid_cyan, solid_cyan);
        outlined_black_square = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.outlined_black_square, outlined_black_square);
        round_rect_solid_black = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.round_rect_solid_black, round_rect_solid_black);
        round_rect_solid_green = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.round_rect_solid_green, round_rect_solid_green);
        grass_tile = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.grass_tile, grass_tile);
        sun_strike_visual_indicator = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.sun_strike_visual_indicator, sun_strike_visual_indicator);
        atlas_training = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.atlas_training, atlas_training);
        atlas_0 = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.atlas0, atlas_0);
        giantAxe = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.giant_axe, giantAxe);
        giantRock1 = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.giant_rock_1, giantRock1);
        giantRock2 = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.giant_rock_2, giantRock2);
        giantRock3 = TextureHelper.loadTexture(MyGL2dRenderer.getContext(), R.drawable.giant_rock_3, giantRock3);
    }
}
