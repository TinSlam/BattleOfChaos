package com.tinslam.battleheart.states;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.buttons.roundbuttons.RoundButton;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.ImageRenderer;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.NameConsts;

import java.util.ArrayList;

public class TrainingSelectorState extends State{
    private final Object lock = new Object();

    private String selectedCharacter = NameConsts.KNIGHT;

    private ImageRenderer charactersPanelImage;
    private RectangleButton[] charactersButtons = new RectangleButton[9];
    private int charactersPanelHeight = GameView.getScreenHeight() * 7 / 10;
    private int charactersPanelYOffset = (GameView.getScreenHeight() - charactersPanelHeight) / 2;
    @SuppressWarnings("SuspiciousNameCombination")
    private int charactersPanelWidth = charactersPanelHeight;
    private int charactersPanelXOffset = (GameView.getScreenWidth() / 2 - charactersPanelWidth) / 2;
    private int charactersGap = (int) (8 * GameView.density());
    private int characterSlotWidth = (charactersPanelWidth - 4 * charactersGap) / 3;
    private int characterSlotHeight = (charactersPanelHeight - 4 * charactersGap) / 3;

    private ImageRenderer trainingPanelImage;
    private ArrayList<RoundButton> trainingList = new ArrayList<>();
    private int trainingPanelXOffset = 2 * charactersPanelXOffset + charactersPanelWidth;
    private int trainingPanelYOffset = charactersPanelYOffset;
    private int trainingPanelWidth = GameView.getScreenWidth() - charactersPanelXOffset - trainingPanelXOffset;
    private int trainingPanelHeight = charactersPanelHeight;

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    @Override
    public void handleBackPressed(){
        ActivityManager.switchToActivity(OpenGL2dActivity.openGL2dActivity, OpenGLActivity.class);
    }

    @Override
    public void startState() {
        initCharactersPanel();
        initTrainingPanel();
    }

    private void initCharactersPanel(){
        charactersPanelImage = new ImageRenderer(charactersPanelXOffset, charactersPanelYOffset, charactersPanelWidth, charactersPanelHeight, TextureData.brown_panel);

        Texture image;
        for(int i = 0; i < 9; i++){
            image = new Texture(PlayerStats.getUnitPortrait(Utils.getCharacterFromInt(i)).getTexture(), characterSlotWidth, characterSlotHeight);
            final int finalI = i;
            charactersButtons[i] = new RectangleButton(charactersPanelXOffset + charactersGap + (finalI % 3) * (charactersGap + characterSlotWidth),
                    charactersPanelYOffset + charactersGap + (finalI / 3) * (charactersGap + characterSlotHeight),
                    image,
                    image,
                    false) {
                @Override
                public boolean onDown() {
                    return true;
                }

                @Override
                public boolean onUp() {
                    selectedCharacter = Utils.getCharacterFromInt(finalI);
                    updateSelectedCharacter();
                    return true;
                }
            };
            if(i > PlayerStats.getUnlockedCharacters().size()) charactersButtons[i].setActive(false);
        }
    }

    private void initTrainingPanel(){
        trainingPanelImage = new ImageRenderer(trainingPanelXOffset, trainingPanelYOffset, trainingPanelWidth, trainingPanelHeight, TextureData.light_brown_panel);

        updateTrainingList();
    }

    private void updateTrainingList(){
        for(Button x : trainingList){
            x.setActive(false);
            x.getTextRenderer().destroy();
        }
        trainingList.clear();
        Texture image = new Texture(TextureData.color_yellow, characterSlotWidth / 2, characterSlotHeight / 2);
        RoundButton r1 = new RoundButton(trainingPanelXOffset + trainingPanelWidth / 3,
                trainingPanelYOffset + trainingPanelHeight / 2, image, image, GameView.string(R.string.speed), true){
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new TrainingSpeedState(selectedCharacter), "");
                return true;
            }
        };
        RoundButton r2 = new RoundButton(trainingPanelXOffset + trainingPanelWidth * 2 / 3,
                trainingPanelYOffset + trainingPanelHeight / 2, image, image, GameView.string(R.string.armor), true){
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                GameView.setState(new TrainingArmorState(selectedCharacter), "");
                return true;
            }
        };
        r1.getTextRenderer().show();
        r2.getTextRenderer().show();
        trainingList.add(r1);
        trainingList.add(r2);
    }

    private void updateSelectedCharacter(){

    }

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        renderCharactersPanel();
        renderTrainingPanel();
    }

    private void renderCharactersPanel(){
        if(charactersPanelImage != null) charactersPanelImage.render(0, 0);

        for(Button x : charactersButtons){
            if(x != null) x.render();
        }

        int i = Utils.getIntegerFromCharacter(selectedCharacter);
        MyGL2dRenderer.drawLabel(charactersPanelXOffset + charactersGap + (i % 3) * (charactersGap + characterSlotWidth),
                charactersPanelYOffset + charactersGap + (i / 3) * (charactersGap + characterSlotHeight),
                characterSlotWidth, characterSlotHeight, TextureData.selected_item_visual, 255);
    }

    private void renderTrainingPanel(){
        if(trainingPanelImage != null) trainingPanelImage.render(0, 0);

        for(Button x : trainingList){
            x.render();
        }
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
