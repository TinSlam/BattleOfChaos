package com.tinslam.battleheart.states;

import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.ImageRenderer;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.items.Item;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.NameConsts;

import java.util.ArrayList;
import java.util.HashMap;

public class ArmoryState extends State{
    private final Object lock = new Object();
    private ArrayList<Button> portraitButtons = new ArrayList<>();

    private Button portraitNextButton, portraitBackButton;
    private String selectedCharacter = NameConsts.KNIGHT;
    private Item selectedItem = null;
    private int portraitIndex = 0;

    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> shownItems = new ArrayList<>();
    private HashMap<Item, Button> itemButtonMap = new HashMap<>();

    private ImageRenderer slotsPanelRenderer;
    private ImageRenderer infoPanelRenderer;
    private ImageRenderer itemPanelRenderer;
    private ImageRenderer goldPanelRenderer;

    private ImageRenderer goldImageRenderer;
    private TextRenderer goldRenderer;

    private Button sellButton;
    private Button equipButton;
    private Button unequipButton;
    private TextRenderer priceTextRenderer;

    private ArrayList<ImageRenderer> equippedVisuals = new ArrayList<>();

    private ArrayList<TextRenderer> stats = new ArrayList<>();

    private int verOffset = (int) (2 * GameView.density());
    private int horOffset = (int) (2 * GameView.density());

    private int goldPanelWidth = GameView.getScreenWidth() / 6;
    private int goldPanelHeight = GameView.getScreenHeight() / 10;
    private int goldPanelXOffset = GameView.getScreenWidth() - 2 * horOffset - goldPanelWidth;
    private int goldPanelYOffset = 2 * verOffset;

    private int portraitWidth = GameView.getScreenWidth() / 11;
    private int portraitHeight = portraitWidth;
    private int portraitXOffset = portraitWidth;
    private int portraitYOffset = 0;

    private int slotsXOffset = 2 * horOffset;
    private int slotsYOffset = (int) (portraitYOffset + portraitHeight + 16 * GameView.density());
    private int slotsWidth = GameView.getScreenWidth() * 4 / 10 - slotsXOffset;
    private int itemsPerLine = 6;
    private int slotsHeight = slotsWidth / itemsPerLine * 3;

    private ImageRenderer[] types = new ImageRenderer[6];

    private int infoPanelXOffset = GameView.getScreenWidth() * 45 / 100;
    private int infoPanelYOffset = (int) (portraitYOffset + portraitHeight + 16 * GameView.density());
    private int infoPanelWidth = GameView.getScreenWidth() * 55 / 100 - 2 * horOffset;
    private int infoPanelHeight = GameView.getScreenHeight() - 2 * verOffset - infoPanelYOffset;

    private int characterWidth = infoPanelWidth / 3;
    private int characterHeight = infoPanelHeight / 2;
    private int characterXOffset = infoPanelXOffset + infoPanelWidth - (infoPanelWidth / 2 - characterWidth) / 2 - characterWidth;
    private int characterYOffset = infoPanelYOffset + infoPanelHeight / 10;

    private int infoStatsXOffset = infoPanelXOffset + infoPanelWidth / 10;
    private int infoStatsYOffset = characterYOffset;
    private int infoStatsWidth = infoPanelWidth / 2 - infoPanelWidth / 5;
    private int infoStatsHeight = characterHeight;
    private int statsWidth = infoStatsWidth;
    private int statsHeight = infoStatsHeight / 8;
    private int statsPerLine = 1;
    private int statsGap = (int) (16 * GameView.density());

    private int buttonWidth = infoPanelWidth / 5;
    private int buttonHeight = infoPanelHeight / 10;
    private int sellButtonXOffset = infoPanelXOffset + infoPanelWidth / 10;
    private int sellButtonYOffset = (int) (infoPanelYOffset + infoPanelHeight - buttonHeight - 8 * GameView.density());
    private int equipButtonXOffset = infoPanelXOffset + infoPanelWidth - infoPanelWidth / 10 - buttonWidth;
    private int equipButtonYOffset = sellButtonYOffset;
    private int sellPriceXOffset = infoPanelXOffset + infoPanelWidth / 2;
    private int sellPriceYOffset = sellButtonYOffset + buttonHeight / 2;

    private int typeWidth = infoPanelWidth / 8;
    private int typeHeight = typeWidth;
    private int typeXOffset = infoPanelXOffset + (infoPanelWidth - 6 * typeWidth) / 2;
    private int typeYOffset = (equipButtonYOffset - (characterYOffset + characterHeight) - typeHeight) / 2 + characterYOffset + characterHeight;

    private int itemPanelXOffset = slotsXOffset;
    private int itemPanelYOffset = (int) (slotsYOffset + slotsHeight + 16 * GameView.density());
    private int itemPanelWidth = slotsWidth;
    private int itemPanelHeight = infoPanelYOffset + infoPanelHeight - itemPanelYOffset;

    private int descXOffset = itemPanelXOffset + itemPanelWidth / 20;
    private int descWidth = itemPanelWidth - itemPanelWidth / 10;
    private int nameXOffset = itemPanelXOffset + itemPanelWidth / 10;
    private int nameYOffset = itemPanelYOffset + itemPanelHeight / 4;
    private int nameWidth = itemPanelWidth / 3;
    private int nameHeight = itemPanelHeight / 6;
    private int imageYOffset = itemPanelYOffset + itemPanelYOffset / 14;
    private int imageWidth = itemPanelWidth / 5;
    @SuppressWarnings("SuspiciousNameCombination")
    private int imageHeight = imageWidth;
    private int imageXOffset = itemPanelXOffset + itemPanelWidth * 3 / 4 - imageWidth / 2;
    private int descYOffset = (int) (imageYOffset + imageHeight + 3 * (imageYOffset - itemPanelYOffset) / 2);

    private TextRenderer desc;
    private TextRenderer name;
    private ImageRenderer image;
    private ImageRenderer characterRenderer;

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void handleBackPressed(){
        ActivityManager.switchToActivity(OpenGL2dActivity.openGL2dActivity, OpenGLActivity.class);
    }

    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    private void initInfo(){
        updateStats();

        for(int i = 0; i < types.length; i++){
            types[i] = new ImageRenderer(typeXOffset + i * typeWidth, typeYOffset,
                    typeWidth, typeHeight, TextureData.unknown);
        }

        priceTextRenderer = new TextRenderer(GameView.string(R.string.sell_price) + " : " + ((selectedItem != null) ? selectedItem.getSellPrice() : "0"),
                sellPriceXOffset,
                sellPriceYOffset,
                infoPanelWidth - 2 * buttonWidth - infoPanelWidth / 5 - 32 * GameView.density(),
                buttonHeight / 2,
                Paint.Align.CENTER,
                false,
                false);
        priceTextRenderer.show();

        //noinspection unused
        sellButton = new RectangleButton(sellButtonXOffset, sellButtonYOffset,
                new Texture(TextureData.button_empty, buttonWidth, buttonHeight),
                new Texture(TextureData.button_empty_hover, buttonWidth, buttonHeight),
                GameView.string(R.string.sell),
                true){
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                final Button self = this;
                new Event() {
                    @Override
                    public void performAction() {
                        if(selectedItem != null) GameView.getState().setConfirmation(true, self, null);
                    }
                };
                return true;
            }

            public void performOnUp(){
                selectedItem.unequip();
                items.remove(selectedItem);
                shownItems.remove(selectedItem);
                selectedItem.sell();
                updateSlots();
                updateGold();
                if(selectedItem == null){
                    equipButton.getTextRenderer().hide();
                    unequipButton.getTextRenderer().hide();
                    equipButton.setActive(false);
                    unequipButton.setActive(false);
                }
            }
        };
        sellButton.getTextRenderer().show();

        equipButton = new RectangleButton(equipButtonXOffset, equipButtonYOffset,
                new Texture(TextureData.button_empty, buttonWidth, buttonHeight),
                new Texture(TextureData.button_empty_hover, buttonWidth, buttonHeight),
                GameView.string(R.string.equip),
                true){
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                new Event() {
                    @Override
                    public void performAction() {
                        if(selectedItem != null){
                            for(Item x : items){
                                if(x.getCarrier().equalsIgnoreCase(selectedCharacter) && x.getType().equalsIgnoreCase(selectedItem.getType())){
                                    x.unequip();
                                    break;
                                }
                            }
                            selectedItem.equip(selectedCharacter);
                            updateEquipped();
                        }
                    }
                };
                return true;
            }
        };

        unequipButton = new RectangleButton(equipButtonXOffset, equipButtonYOffset,
                new Texture(TextureData.button_empty, buttonWidth, buttonHeight),
                new Texture(TextureData.button_empty_hover, buttonWidth, buttonHeight),
                GameView.string(R.string.unequip),
                true){
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                new Event() {
                    @Override
                    public void performAction() {
                        if(selectedItem != null){
                            selectedItem.unequip();
                            updateEquipped();
                        }
                    }
                };
                return true;
            }
        };

        unequipButton.setActive(false);
        equipButton.setActive(false);

        characterRenderer = new ImageRenderer(characterXOffset, characterYOffset,
                characterWidth, characterHeight,
                PlayerStats.getUnitPortrait(selectedCharacter).getTexture());
    }

    private void updateEquipped(){
        try{
            if(selectedItem.isEquipped(selectedCharacter)){
                equipButton.setActive(false);
                unequipButton.setActive(true);
                equipButton.getTextRenderer().hide();
                unequipButton.getTextRenderer().show();
            }else{
                equipButton.setActive(true);
                unequipButton.setActive(false);
                equipButton.getTextRenderer().show();
                unequipButton.getTextRenderer().hide();
            }
        }catch(Exception ignored){}
        equippedVisuals.clear();
        int width = slotsWidth / itemsPerLine;
        int i = 0;
        int w = width / 5;
        int h = width / 5;
        for(Item x : shownItems){
            if(!x.getCarrier().isEmpty()){
                if(x.getCarrier().equalsIgnoreCase(selectedCharacter)){
                    equippedVisuals.add(new ImageRenderer(slotsXOffset + (i % itemsPerLine) * width + width - w,
                            slotsYOffset + (i / itemsPerLine) * width + width - h,
                            w, h, TextureData.color_yellow));
                }else{
                    equippedVisuals.add(new ImageRenderer(slotsXOffset + (i % itemsPerLine) * width + width - w,
                            slotsYOffset + (i / itemsPerLine) * width + width - h,
                            w, h, TextureData.color_blue));
                }
            }
            i++;
        }
        updateTypes();
    }

    private void updateTypes(){
        try{
            for(ImageRenderer x : types) if(x != null) x.setImage(TextureData.unknown);
            boolean amuletFlag = false;
            for(Item x : items){
                if(x.getCarrier().equalsIgnoreCase(selectedCharacter)){
                    switch(x.getType()){
                        case NameConsts.ITEM_HELMET :
                            if(types[0] != null) types[0].setImage(x.getImage());
                            break;

                        case NameConsts.ITEM_WEAPON :
                            if(types[1] != null) types[1].setImage(x.getImage());
                            break;

                        case NameConsts.ITEM_ARMOR :
                            if(types[2] != null) types[2].setImage(x.getImage());
                            break;

                        case NameConsts.ITEM_BOOTS :
                            if(types[3] != null) types[3].setImage(x.getImage());
                            break;

                        case NameConsts.ITEM_AMULET :
                            if(!amuletFlag){
                                if(types[4] != null) types[4].setImage(x.getImage());
                                amuletFlag = true;
                            }else{
                                if(types[5] != null) types[5].setImage(x.getImage());
                            }
                            break;
                    }
                }
            }
        }catch(Exception ignored){ignored.printStackTrace();}
    }

    private void updateStats(){
        for(TextRenderer x : stats){
            x.destroy();
        }
        stats.clear();
        if(selectedItem != null){
            for(int i = 0; i < selectedItem.getProperties().size(); i++){
                int temp = 3 - (selectedItem.getProperties().size() - 1) / 2;
                stats.add(new TextRenderer(selectedItem.getProperties().get(i),
                        infoStatsXOffset + (i % statsPerLine) * (statsWidth + statsGap),
                        infoStatsYOffset + (i / statsPerLine) * statsHeight + statsHeight * temp / 2,
                        statsWidth,
                        statsHeight * 2 / 3,
                        Paint.Align.LEFT,
                        false,
                        false));
                stats.get(i).show();
            }
        }
    }

    private void initGold(){
        goldImageRenderer = new ImageRenderer(goldPanelXOffset + goldPanelWidth / 10,
                goldPanelYOffset + goldPanelHeight / 6, goldPanelHeight * 2 / 3, goldPanelHeight * 2 / 3, TextureData.gold);

        updateGold();

        goldPanelRenderer = new ImageRenderer(goldPanelXOffset, goldPanelYOffset, goldPanelWidth, goldPanelHeight, TextureData.yellow_panel);
    }

    private void initPortraits(){
        Texture image = new Texture(TextureData.blue_arrow_right, portraitWidth, portraitHeight);
        Texture image2 = image;
        portraitBackButton = new RectangleButton(portraitXOffset - portraitWidth, portraitYOffset, image2, image2, true) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                new Event() {
                    @Override
                    public void performAction() {
                        portraitIndex = Utils.max(0, portraitIndex - 1);
                        updatePortraitPositions();
                    }
                };
                return true;
            }
        };
        portraitBackButton.toggleFlipped();
        portraitNextButton = new RectangleButton(portraitXOffset + 5 * portraitWidth, portraitYOffset, image, image, true) {
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                new Event() {
                    @Override
                    public void performAction() {
                        portraitIndex = Utils.min(4, portraitIndex + 1);
                        updatePortraitPositions();
                    }
                };
                return true;
            }
        };
        for(int i = 0; i < 9; i++){
            final String character = Utils.getCharacterFromInt(i);
            image = new Texture(PlayerStats.getUnitPortrait(character).getTexture(), portraitWidth, portraitHeight);
            portraitButtons.add(new RectangleButton(0, 0, image, image, true) {
                @Override
                public boolean onDown() {
                    return true;
                }

                @Override
                public boolean onUp() {
                    new Event() {
                        @Override
                        public void performAction() {
                            selectedCharacter = character;
                            updateShownCharacter();
                        }
                    };
                    return true;
                }
            });
        }
        updatePortraitPositions();
    }

    private void initSlots(){
        int width = slotsWidth / itemsPerLine;
        for(final Item x : items){
            //noinspection SuspiciousNameCombination
            Texture image = new Texture(x.getImage(), width, width);
            final Button button = new RectangleButton(0, 0,
                    image,
                    image,
                    true) {
                @Override
                public boolean onDown() {
                    return true;
                }

                @Override
                public boolean onUp() {
                    new Event() {
                        @Override
                        public void performAction() {
                            selectedItem = x;
                            updateSelectedItem();
                        }
                    };
                    return true;
                }
            };
            new Event() {
                @Override
                public void performAction() {
                    itemButtonMap.put(x, button);
                }
            };
        }
        new Event() {
            @Override
            public void performAction() {
                updateSlots();
            }
        };
    }

    private void updateSlots(){
        updateShownItems();
        int width = slotsWidth / itemsPerLine;
        int i = 0;
        for(final Item x : items){
            Button button = itemButtonMap.get(x);
            if(shownItems.contains(x)){
                button.setPosition(slotsXOffset + (i % itemsPerLine) * width,
                        slotsYOffset + (i / itemsPerLine) * width);
                button.setActive(true);
                i++;
            }else{
                button.setActive(false);
            }
        }
    }

    private void updateShownItems(){
        shownItems.clear();
        for(Item x : items){
            if(x.getCharacters().contains(selectedCharacter)){
                shownItems.add(x);
            }
        }
        if(!shownItems.isEmpty()){
            selectedItem = shownItems.get(0);
        }else{
            selectedItem = null;
        }
        updateSelectedItem();
    }

    private void updateSelectedItem(){
        if(priceTextRenderer != null) priceTextRenderer.setText(GameView.string(R.string.sell_price) + " : " + ((selectedItem == null) ? 0 : selectedItem.getSellPrice()));
        updateType();
        updateEquipped();
        updateStats();
    }

    private void updateType(){
        if(image != null) image.setImage((selectedItem != null) ? selectedItem.getImage() : TextureData.unknown);
        if(name != null) name.setText((selectedItem != null) ? selectedItem.getName() : "");
        if(desc != null) desc.setText((selectedItem != null) ? selectedItem.getDescription() : "");
    }

    private void initType(){
        image = new ImageRenderer(imageXOffset, imageYOffset, imageWidth, imageHeight, (selectedItem != null) ? selectedItem.getImage() : TextureData.unknown);
        name = new TextRenderer((selectedItem != null) ? selectedItem.getName() : "",
                nameXOffset,
                nameYOffset,
                nameWidth,
                nameHeight / 2,
                Paint.Align.LEFT,
                false, false);
        name.show();
        desc = new TextRenderer((selectedItem != null) ? selectedItem.getDescription() : "",
                descXOffset,
                descYOffset,
                descWidth,
                itemPanelHeight / 12,
                Paint.Align.LEFT,
                true, false);
        desc.show();
    }

    @Override
    public void startState() {
        items.addAll(PlayerStats.items);
        updateShownItems();
        initPortraits();
        initSlots();
        initInfo();
        initGold();
        initType();

        updateEquipped();

        infoPanelRenderer = new ImageRenderer(infoPanelXOffset, infoPanelYOffset, infoPanelWidth, infoPanelHeight, TextureData.light_brown_panel);

        slotsPanelRenderer = new ImageRenderer(slotsXOffset, slotsYOffset, slotsWidth, slotsHeight, TextureData.brown_panel);

        itemPanelRenderer = new ImageRenderer(itemPanelXOffset, itemPanelYOffset, itemPanelWidth, itemPanelHeight, TextureData.light_brown_panel);

    }

    private void updatePortraitPositions(){
        for(Button x : portraitButtons) x.setActive(false);
        for(int i = 0; i < 5; i++){
            portraitButtons.get(i + portraitIndex).setPosition(portraitXOffset + i * portraitWidth, portraitYOffset);
            portraitButtons.get(i + portraitIndex).setActive(true);
        }
    }

    private void updateGold(){
        if(goldRenderer != null) goldRenderer.destroy();
        goldRenderer = new TextRenderer(PlayerStats.gold + "",
                goldPanelXOffset + goldPanelWidth * 2 / 3,
                goldPanelYOffset + goldPanelHeight / 2,
                goldPanelWidth * 2 / 3 - 8 * GameView.density(),
                goldPanelHeight / 3,
                Paint.Align.CENTER,
                false, false);
        goldRenderer.show();
    }

    private void updateShownCharacter(){
        updateSlots();
        characterRenderer.setImage(PlayerStats.getUnitPortrait(selectedCharacter).getTexture());
        updateSelectedItem();
    }

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        renderPortraits();
        renderInfoPanel();
        renderSlotsPanel();
        renderItemPanel();
        renderGoldPanel();
    }

    private void renderItemPanel(){
        if(itemPanelRenderer != null) itemPanelRenderer.render(0, 0);
        if(image != null) image.render(0, 0);
        if(characterRenderer != null) characterRenderer.renderFlipped(0, 0);
    }

    private void renderGoldPanel(){
        if(goldPanelRenderer != null) goldPanelRenderer.render(0, 0);
        if(goldImageRenderer != null) goldImageRenderer.render(0, 0);
    }

    private void renderInfoPanel(){
        if(infoPanelRenderer != null) infoPanelRenderer.render(0, 0);

        if(equipButton != null) if(equipButton.isActive()) equipButton.render();
        if(unequipButton != null) if(unequipButton.isActive()) unequipButton.render();
        if(sellButton != null) sellButton.render();
        for(ImageRenderer x : types){
            if(x != null) x.render(0, 0);
        }
    }

    private void renderSlotsPanel(){
        if(slotsPanelRenderer != null) slotsPanelRenderer.render(0, 0);

        for(Item x : shownItems){
            Button button = itemButtonMap.get(x);
            if(button == null) continue;
            button.render();
            if(selectedItem == x){
                MyGL2dRenderer.drawLabel(button.getX(), button.getY(), button.getWidth(), button.getHeight(), TextureData.selected_item_visual, 255);
            }
        }
        for(ImageRenderer x : equippedVisuals){
            x.render(0, 0);
        }
    }

    private void renderPortraits(){
        portraitBackButton.render();
        portraitNextButton.render();
        for(int i = 0; i < 5; i++){
            portraitButtons.get(i + portraitIndex).render();
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
