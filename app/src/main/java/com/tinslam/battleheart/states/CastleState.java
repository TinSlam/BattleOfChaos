package com.tinslam.battleheart.states;

import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.NameConsts;

import java.util.ArrayList;

public class CastleState extends State{
    private ArrayList<TextRenderer> texts = new ArrayList<>();
    private final Object textsLock = new Object();
    private byte chosenSlot = 0;
    private Animation animation;

    private void drawUnlockedCharacters(){
        int i = 0;
        Texture portrait;
        if(chosenSlot >= PlayerStats.getUnlockedCharacters().size()){
            MyGL2dRenderer.drawLabel(GameView.getScreenWidth() - (3 - chosenSlot % 3) * Consts.PORTRAIT_WIDTH - GameView.getScreenWidth() / 20,
                    (chosenSlot / 3) * Consts.PORTRAIT_HEIGHT + GameView.getScreenHeight() / 8,
                    Consts.PORTRAIT_WIDTH, Consts.PORTRAIT_HEIGHT, TextureData.solid_yellow, 255);
            drawCharacterInformation("");
        }
        for(String x : PlayerStats.getUnlockedCharacters()){
            if(i == chosenSlot){
                MyGL2dRenderer.drawLabel(GameView.getScreenWidth() - (3 - i % 3) * Consts.PORTRAIT_WIDTH - GameView.getScreenWidth() / 20,
                        (i / 3) * Consts.PORTRAIT_HEIGHT + GameView.getScreenHeight() / 8,
                        Consts.PORTRAIT_WIDTH, Consts.PORTRAIT_HEIGHT,
                        TextureData.solid_yellow, 255);
                drawCharacterInformation(x);
            }
            portrait = PlayerStats.getUnitPortrait(x);
            assert portrait != null;
            MyGL2dRenderer.drawLabel(GameView.getScreenWidth() - (3 - i % 3) * Consts.PORTRAIT_WIDTH - GameView.getScreenWidth() / 20,
                    (i / 3) * Consts.PORTRAIT_HEIGHT + GameView.getScreenHeight() / 8,
                    portrait.getWidth(), portrait.getHeight(), portrait.getTexture(), 255);
            i++;
        }
        for(i = 0; i < 9; i++){
            MyGL2dRenderer.drawLabel(GameView.getScreenWidth() - (3 - i % 3) * Consts.PORTRAIT_WIDTH - GameView.getScreenWidth() / 20,
                    (i / 3) * Consts.PORTRAIT_HEIGHT + GameView.getScreenHeight() / 8,
                    Consts.PORTRAIT_WIDTH, Consts.PORTRAIT_HEIGHT, TextureData.selected_item_visual, 255);
        }
    }

    private void drawCharacterInformation(String character) {
        MyGL2dRenderer.drawLabel((int) (4 * GameView.density()), (int) (4 * GameView.density()),
                GameView.getScreenWidth() / 2, GameView.getScreenHeight(), TextureData.solid_cyan, 255);

        int maxExpBarWidth = (int) (GameView.getScreenWidth() / 2 - 64 * GameView.density());
        int xOffset = (int) (32 * GameView.density());
        int yOffset = (int) (GameView.getScreenHeight() / 2 - 48 * GameView.density());
        int exp = 0;
        int level = 1;
        int expNeeded;

        switch(character){
            case NameConsts.KNIGHT :
            case NameConsts.ARCHER :
                texts.get(0).setText(GameView.string(R.string.hp) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitHp(character) + PlayerStats.getUnitExtraHp(character)));
                texts.get(1).setText(GameView.string(R.string.damage) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitDamage(character) + PlayerStats.getUnitExtraDamage(character)));
                texts.get(2).setText(GameView.string(R.string.armor) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitArmor(character) + PlayerStats.getUnitExtraArmor(character)));
                float speed = PlayerStats.getUnitSpeed(character) + PlayerStats.getUnitExtraSpeed(character);
                texts.get(3).setText(GameView.string(R.string.speed) + " : " + ((Consts.MAXIMUM_SPEED < speed) ? (Consts.MAXIMUM_SPEED + " " + GameView.string(R.string.maximum_reached)) : (Consts.MINIMUM_SPEED > speed) ? (Consts.MINIMUM_SPEED + " " + GameView.string(R.string.minimum_reached)) : Utils.formatStringOneRadixPoint(speed)));
                int range = (int) PlayerStats.getUnitAttackRange(character);
                if(range == -1){
                    texts.get(4).setText(GameView.string(R.string.attack_range) + " : " + GameView.string(R.string.melee));
                }else{
                    texts.get(4).setText(GameView.string(R.string.attack_range) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitAttackRange(character) + PlayerStats.getUnitExtraAttackRange(character)));
                }
                texts.get(5).setText(GameView.string(R.string.attack_cooldown) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitAttackCd(character) + PlayerStats.getUnitExtraAttackCd(character)));
                level = PlayerStats.getUnitLvl(character);
                exp = PlayerStats.getUnitExp(character) - Utils.getExpNeeded(PlayerStats.getUnitLvl(character) - 1);
                expNeeded = Utils.getExpNeeded(level) - PlayerStats.getUnitExp(character);
                texts.get(7).setText(GameView.string(R.string.experience_needed_for_next_level) + " : " + expNeeded);
                texts.get(6).setMaxPaintSize(GameView.getScreenHeight() / 30);
                texts.get(6).setText(GameView.string(R.string.current_level) + " : " + PlayerStats.getUnitLvl(character));
                break;

            case NameConsts.PRIEST :
                texts.get(0).setText(GameView.string(R.string.hp) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitHp(character) + PlayerStats.getUnitExtraHp(character)));
                texts.get(1).setText(GameView.string(R.string.heal) + " : " + Utils.formatStringOneRadixPoint(-PlayerStats.getUnitDamage(character) - PlayerStats.getUnitExtraDamage(character)));
                texts.get(2).setText(GameView.string(R.string.armor) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitArmor(character) + PlayerStats.getUnitExtraArmor(character)));
                speed = PlayerStats.getUnitSpeed(character) + PlayerStats.getUnitExtraSpeed(character);
                texts.get(3).setText(GameView.string(R.string.speed) + " : " + ((Consts.MAXIMUM_SPEED < speed) ? (Consts.MAXIMUM_SPEED + " " + GameView.string(R.string.maximum_reached)) : (Consts.MINIMUM_SPEED > speed) ? (Consts.MINIMUM_SPEED + " " + GameView.string(R.string.minimum_reached)) : Utils.formatStringOneRadixPoint(speed)));
                texts.get(4).setText(GameView.string(R.string.attack_range) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitAttackRange(character) + PlayerStats.getUnitExtraAttackRange(character)));
                texts.get(5).setText(GameView.string(R.string.attack_cooldown) + " : " + Utils.formatStringOneRadixPoint(PlayerStats.getUnitAttackCd(character) + PlayerStats.getUnitExtraAttackCd(character)));
                level = PlayerStats.getUnitLvl(character);
                exp = PlayerStats.getUnitExp(character) - Utils.getExpNeeded(PlayerStats.getUnitLvl(character) - 1);
                expNeeded = Utils.getExpNeeded(level) - PlayerStats.getUnitExp(character);
                texts.get(7).setText(GameView.string(R.string.experience_needed_for_next_level) + " : " + expNeeded);
//                    texts.get(6).setMaxPaintSize(texts.get(7).getTextSize());
                texts.get(6).setMaxPaintSize(GameView.getScreenHeight() / 30);
                texts.get(6).setText(GameView.string(R.string.current_level) + " : " + PlayerStats.getUnitLvl(character));
                break;

            default :
                texts.get(0).setText(GameView.string(R.string.hp) + " : " + GameView.string(R.string.unknown_value));
                texts.get(1).setText(GameView.string(R.string.damage) + " : " + GameView.string(R.string.unknown_value));
                texts.get(2).setText(GameView.string(R.string.armor) + " : " + GameView.string(R.string.unknown_value));
                texts.get(3).setText(GameView.string(R.string.speed) + " : " + GameView.string(R.string.unknown_value));
                texts.get(4).setText(GameView.string(R.string.attack_range) + " : " + GameView.string(R.string.unknown_value));
                texts.get(5).setText(GameView.string(R.string.attack_cooldown) + " : " + GameView.string(R.string.unknown_value));
                texts.get(6).setText("");
                texts.get(7).setText(GameView.string(R.string.current_level) + " : " + 0);
                break;
        }

        int expBarWidth = (int) (exp * (float) maxExpBarWidth /
                (Utils.getExpNeeded(level) - Utils.getExpNeeded(level - 1)));
        MyGL2dRenderer.drawLabel((int) (xOffset - 2 * GameView.density()), (int) (yOffset - 2 * GameView.density()), (int) (maxExpBarWidth + 4 * GameView.density()), (int) (12 * GameView.density() + 4 * GameView.density()), TextureData.round_rect_solid_black, 255);
        MyGL2dRenderer.drawLabel(xOffset, yOffset, expBarWidth, (int) (12 * GameView.density()), TextureData.round_rect_solid_green, 255);
    }

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void handleBackPressed() {
        ActivityManager.switchToActivity(OpenGL2dActivity.openGL2dActivity, OpenGLActivity.class);
    }

    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    @Override
    public void startState() {
        texts.add(new TextRenderer("",
                16 * GameView.density(),
                GameView.getScreenHeight() / 2,
                GameView.getScreenWidth() / 2 - 32 * GameView.density(),
                GameView.getScreenHeight() / 24,
                Paint.Align.LEFT, false, false));

        texts.add(new TextRenderer("",
                16 * GameView.density(),
                GameView.getScreenHeight() / 2 + GameView.getScreenHeight() / 12,
                GameView.getScreenWidth() / 2 - 32 * GameView.density(),
                GameView.getScreenHeight() / 24,
                Paint.Align.LEFT, false, false));

        texts.add(new TextRenderer("",
                16 * GameView.density(),
                GameView.getScreenHeight() / 2 + 2 * GameView.getScreenHeight() / 12,
                GameView.getScreenWidth() / 2 - 32 * GameView.density(),
                GameView.getScreenHeight() / 24,
                Paint.Align.LEFT, false, false));

        texts.add(new TextRenderer("",
                16 * GameView.density(),
                GameView.getScreenHeight() / 2 + 3 * GameView.getScreenHeight() / 12,
                GameView.getScreenWidth() / 2 - 32 * GameView.density(),
                GameView.getScreenHeight() / 24,
                Paint.Align.LEFT, false, false));

        texts.add(new TextRenderer("",
                16 * GameView.density(),
                GameView.getScreenHeight() / 2 + 4 * GameView.getScreenHeight() / 12,
                GameView.getScreenWidth() / 2 - 32 * GameView.density(),
                GameView.getScreenHeight() / 24,
                Paint.Align.LEFT, false, false));

        texts.add(new TextRenderer("",
                16 * GameView.density(),
                GameView.getScreenHeight() / 2 + 5 * GameView.getScreenHeight() / 12,
                GameView.getScreenWidth() / 2 - 32 * GameView.density(),
                GameView.getScreenHeight() / 24,
                Paint.Align.LEFT, false, false));

        texts.add(new TextRenderer("",
                GameView.getScreenWidth() / 4,
                GameView.getScreenHeight() / 2 - 80 * GameView.density(),
                GameView.getScreenWidth() / 2 - 96 * GameView.density(),
                GameView.getScreenHeight() / 24,
                Paint.Align.CENTER, false, false));

        texts.add(new TextRenderer("",
                GameView.getScreenWidth() / 4,
                GameView.getScreenHeight() / 2 - 64 * GameView.density(),
                GameView.getScreenWidth() / 2 - 96 * GameView.density(),
                GameView.getScreenHeight() / 24,
                Paint.Align.CENTER, false, false));

        for(TextRenderer textRenderer : texts){
            textRenderer.show();
        }

        setAnimation(NameConsts.KNIGHT);
    }

    private void setAnimation(String character){
        if(animation != null) animation.hardStop();
        animation = PlayerStats.getUnitAnimation(character);
        if(animation != null) animation.reset();
    }

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        drawUnlockedCharacters();
        if(animation != null) animation.render((GameView.getScreenWidth() / 2 - 8 * GameView.density()) / 2 - animation.getWidth() / 2, 16 * GameView.density());
    }

    @Override
    public void renderOver() {

    }

    @Override
    public boolean onActionDown(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onActionPointerDown(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onActionMove(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onActionUp(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        for(byte i = 0; i < 9; i++){
            if(Utils.isInRect(x, y,
                    GameView.getScreenWidth() - (3 - i % 3) * Consts.PORTRAIT_WIDTH - GameView.getScreenWidth() / 20,
                    (i / 3) * Consts.PORTRAIT_HEIGHT + GameView.getScreenHeight() / 8,
                    GameView.getScreenWidth() - (3 - i % 3) * Consts.PORTRAIT_WIDTH + Consts.PORTRAIT_WIDTH - GameView.getScreenWidth() / 20,
                    (i / 3) * Consts.PORTRAIT_HEIGHT + Consts.PORTRAIT_HEIGHT + GameView.getScreenHeight() / 8)){
                chosenSlot = i;
                try{
                    setAnimation(PlayerStats.getUnlockedCharacters().get(i));
                }catch(Exception e){
                    if(animation != null){
                        animation.hardStop();
                        animation = null;
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onActionPointerUp(MotionEvent event) {
        return false;
    }

    @Override
    public void endState() {

    }
}
