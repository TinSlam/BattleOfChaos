package com.tinslam.battleheart.states;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.ImageRenderer;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.GameActivity;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.items.Item;
import com.tinslam.battleheart.items.boots.LightThreads;
import com.tinslam.battleheart.items.armors.GiantShield;
import com.tinslam.battleheart.items.armors.SimpleShield;
import com.tinslam.battleheart.items.weapons.MagicWand;
import com.tinslam.battleheart.items.weapons.SultansDagger;
import com.tinslam.battleheart.items.weapons.WoodenBow;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.NameConsts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopState extends State{
    private String[] categories = new String[] {"All", NameConsts.ITEM_HELMET, NameConsts.ITEM_WEAPON, NameConsts.ITEM_ARMOR, NameConsts.ITEM_BOOTS, NameConsts.ITEM_AMULET};
    private String currentCategory = categories[0];
    private final Object itemsLock = new Object();
    private HashMap<Item, Button> itemButtonMap = new HashMap<>();
    private HashMap<String, ImageRenderer> stringImageMap = new HashMap<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> shownItems = new ArrayList<>();
    private int verOffset = (int) (2 * GameView.density());
    private int horOffset = (int) (2 * GameView.density());
    private int categoriesXOffset = 2 * horOffset;
    private int categoriesYOffset = 2 * verOffset;
    private int categoriesHeight = GameView.getScreenHeight() / 10;
    private int goldPanelWidth = GameView.getScreenWidth() / 6;
    private int goldPanelHeight = GameView.getScreenHeight() / 10;
    private int goldPanelXOffset = GameView.getScreenWidth() - 2 * horOffset - goldPanelWidth;
    private int goldPanelYOffset = 2 * verOffset;
    private int slotsXOffset = 2 * horOffset;
    private int slotsYOffset = (int) (categoriesYOffset + categoriesHeight + 16 * GameView.density());
    private int slotsWidth = GameView.getScreenWidth() * 4 / 10 - slotsXOffset;
    private int itemsPerLine = 6;
    private int slotsHeight = slotsWidth / itemsPerLine * 4;
    private int infoPanelXOffset = GameView.getScreenWidth() * 45 / 100;
    private int infoPanelYOffset = (int) (categoriesYOffset + categoriesHeight + 16 * GameView.density());
    private int infoPanelWidth = GameView.getScreenWidth() * 55 / 100 - 2 * horOffset;
    private int infoPanelHeight = GameView.getScreenHeight() - 2 * verOffset - infoPanelYOffset;
    private int infoNameXOffset = (int) (infoPanelXOffset + 32 * GameView.density());
    private int infoNameYOffset = infoPanelYOffset + infoPanelHeight / 6;
//    private int infoNameWidth = infoPanelWidth / 3;
    private int infoNameWidth = infoPanelWidth / 2;
    private int infoNameHeight = infoPanelHeight / 8;
    private int infoImageXOffset = infoPanelXOffset + infoPanelWidth * 2 / 3;
    private int infoImageHeight = infoPanelHeight / 6;
    private int infoImageWidth = infoImageHeight;
    private int infoImageYOffset = infoNameYOffset - infoImageHeight / 2;
    private int infoStatsWidth = (int) (infoPanelWidth - 64 * GameView.density());
    private int infoStatsXOffset = infoPanelXOffset + (infoPanelWidth - infoStatsWidth) / 2;
    private int infoStatsYOffset = infoPanelYOffset + infoPanelHeight / 3;
    private int infoStatsHeight = infoPanelHeight / 3;
    private int statsWidth = (int) (infoStatsWidth / 2);
    private int statsHeight = infoStatsHeight / 4;
    private int statsPerLine = 2;
    private int statsGap = (int) (16 * GameView.density());
    private int infoDescWidth = (int) (infoPanelWidth - 64 * GameView.density());
    private int infoDescXOffset = infoPanelXOffset + (infoPanelWidth - infoDescWidth) / 2;
    private int infoDescYOffset = infoPanelYOffset + infoPanelHeight * 3 / 4;
    private int infoPaintSize = infoPanelHeight / 20;
    private int categoriesWidth = GameView.getScreenWidth() / 10;
    private int buyPanelXOffset = slotsXOffset;
    private int buyPanelYOffset = (int) (slotsYOffset + slotsHeight + 16 * GameView.density());
    private int shopPanelYOffset = (int) (slotsYOffset + slotsHeight + 16 * GameView.density());
    private int buyPanelHeight = infoPanelYOffset + infoPanelHeight - shopPanelYOffset;
    private int shopPanelHeight = infoPanelYOffset + infoPanelHeight - shopPanelYOffset;
    private int shopPanelWidth = shopPanelHeight;
    private int buyPanelWidth = (int) (slotsWidth - shopPanelWidth - 16 * GameView.density());
    private int shopPanelXOffset = (int) (buyPanelXOffset + buyPanelWidth + 16 * GameView.density());
    private RectangleButton buyButton;
    private RectangleButton buyMoreCoins;
    private TextRenderer priceText;
    private Item selectedItem;

    private TextRenderer itemName;
    private ArrayList<TextRenderer> itemStats = new ArrayList<>();
    private TextRenderer itemDescription;
    private ImageRenderer itemImage;

    private ImageRenderer goldPanelImageRenderer;
    private ImageRenderer infoPanelImageRenderer;
    private ImageRenderer slotsPanelImageRenderer;
    private ImageRenderer buyPanelImageRenderer;
    private ImageRenderer shopPanelImageRenderer;

    private ImageRenderer priceImageRenderer;
    private ImageRenderer goldImageRenderer;
    private TextRenderer goldRenderer;

    private ArrayList<ImageRenderer> compatibleCharactersRenderers = new ArrayList<>();

    private static Paint paint = new Paint();

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
        int buttonWidth = buyPanelWidth * 4 / 5;
        int buttonHeight = buyPanelHeight / 4;
        buyButton = new RectangleButton(buyPanelXOffset + buyPanelWidth / 10, buyPanelYOffset + buyPanelHeight - buyPanelHeight / 8 - buttonHeight,
                new Texture(TextureData.button_empty, buttonWidth, buttonHeight),
                new Texture(TextureData.button_empty_hover, buttonWidth, buttonHeight),
                GameView.string(R.string.buy),
                true){
            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onUp() {
                if(selectedItem != null && PlayerStats.gold >= selectedItem.getPrice()) GameView.getState().setConfirmation(true, this, null);
                return true;
            }

            /**
             * Is performed once action is confirmed.
             */
            public void performOnUp(){
                selectedItem.buy();
                updateGold();
            }
        };
        buyButton.getTextRenderer().show();

        for(int i = 0; i < 9; i++){
            String character = Utils.getCharacterFromInt(i);
            compatibleCharactersRenderers.add(new ImageRenderer(shopPanelXOffset + (i % 3) * shopPanelHeight / 3,
                    shopPanelYOffset + (i / 3) * shopPanelHeight / 3, shopPanelHeight / 3, shopPanelHeight / 3, TextureData.unknown));
            stringImageMap.put(character, new ImageRenderer(shopPanelXOffset + (i % 3) * shopPanelHeight / 3,
                    shopPanelYOffset + (i / 3) * shopPanelHeight / 3,
                    shopPanelHeight / 3, shopPanelHeight / 3,
                    PlayerStats.getUnitPortrait(character).getTexture()));
        }

        items.add(new LightThreads(""));
        items.add(new GiantShield(""));
        items.add(new SimpleShield(""));
        items.add(new MagicWand(""));
        items.add(new WoodenBow(""));
        items.add(new SultansDagger(""));
        selectedItem = items.get(0);
        updateItemShown();

        goldImageRenderer = new ImageRenderer(goldPanelXOffset + goldPanelWidth / 10,
                goldPanelYOffset + goldPanelHeight / 6, goldPanelHeight * 2 / 3, goldPanelHeight * 2 / 3, TextureData.gold);

        priceImageRenderer = new ImageRenderer((int) (buyPanelXOffset + 8 * GameView.density()),
                buyPanelYOffset + buyPanelHeight / 3 - buyPanelWidth / 4,
                buyPanelWidth / 4, buyPanelWidth / 4, TextureData.gold);

        updateGold();

        int width = slotsWidth / itemsPerLine;
        for(int i = 0; i < items.size(); i++){
            Texture image = new Texture(items.get(i).getImage(), width, width);
            final int finalI = i;
            itemButtonMap.put(items.get(i), new RectangleButton(slotsXOffset + (finalI % itemsPerLine) * width,
                    slotsYOffset + (finalI / itemsPerLine) * width,
                    image,
                    image,
                    true) {
                @Override
                public boolean onDown() {
                    return true;
                }

                @Override
                public boolean onUp() {
                    selectedItem = items.get(finalI);
                    updateItemShown();
                    return true;
                }
            });
        }

        goldPanelImageRenderer = new ImageRenderer(goldPanelXOffset, goldPanelYOffset, goldPanelWidth, goldPanelHeight, TextureData.yellow_panel);

        infoPanelImageRenderer = new ImageRenderer(infoPanelXOffset, infoPanelYOffset, infoPanelWidth, infoPanelHeight, TextureData.light_brown_panel);

        slotsPanelImageRenderer = new ImageRenderer(slotsXOffset, slotsYOffset, slotsWidth, slotsHeight, TextureData.brown_panel);

        buyPanelImageRenderer = new ImageRenderer(buyPanelXOffset, buyPanelYOffset, buyPanelWidth, buyPanelHeight, TextureData.light_brown_panel);

        shopPanelImageRenderer = new ImageRenderer(shopPanelXOffset, shopPanelYOffset, shopPanelWidth, shopPanelHeight, TextureData.brown_panel);

        Texture image = new Texture(TextureData.button_empty, categoriesWidth, categoriesHeight);
        Texture imageOnClick = new Texture(TextureData.button_empty_hover, categoriesWidth, categoriesHeight);
        int i = 0;
        for(final String x : categories){
            RectangleButton button = new RectangleButton(categoriesXOffset + i * categoriesWidth, categoriesYOffset, image, imageOnClick, Utils.getStringFromItemCategory(x), false) {
                @Override
                public boolean onDown() {
                    return true;
                }

                @Override
                public boolean onUp() {
                    currentCategory = x;
                    updateItemPositions();
                    return true;
                }
            };
            getButtons().add(button);
            button.getTextRenderer().show();
            i++;
        }
        updateItemPositions();
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

    private void updateItemShown(){
        if(priceText != null) priceText.destroy();
        priceText = new TextRenderer("" + selectedItem.getPrice(),
                buyPanelXOffset + buyPanelWidth * 5 / 8,
                buyPanelYOffset + buyPanelHeight / 4,
                buyButton.getWidth() * 3 / 4 - 8 * GameView.density(),
                buyPanelHeight / 8,
                Paint.Align.CENTER,
                false, false);
        priceText.show();
        if(itemName != null) itemName.destroy();
        itemName = new TextRenderer(selectedItem.getName(),
                infoNameXOffset,
                infoNameYOffset,
                infoNameWidth,
                infoNameHeight,
                Paint.Align.LEFT,
                false, false);
        itemName.show();
        itemImage = new ImageRenderer(infoImageXOffset, infoImageYOffset, infoImageWidth, infoImageHeight, selectedItem.getImage());
        ArrayList<String> stats = selectedItem.getProperties();
        for(TextRenderer tr : itemStats){
            tr.destroy();
        }
        itemStats.clear();
        for(int i = 0; i < stats.size(); i++){
            int temp = 3 - (stats.size() - 1) / 2;
            TextRenderer tr = new TextRenderer(stats.get(i),
                    infoStatsXOffset + (i % statsPerLine) * (statsWidth + statsGap),
                    infoStatsYOffset + (i / statsPerLine) * statsHeight + statsHeight * temp / 2,
                    statsWidth,
                    statsHeight / 2,
                    Paint.Align.LEFT,
                    false, false);
            tr.show();
            itemStats.add(tr);
        }
        if(itemDescription != null) itemDescription.destroy();
        itemDescription = new TextRenderer(selectedItem.getDescription(),
                infoDescXOffset,
                infoDescYOffset,
                infoDescWidth,
                infoPaintSize,
                Paint.Align.LEFT,
                true, false);
        itemDescription.show();
    }

    private void updateItemPositions(){
        shownItems.clear();
        for(Object button : itemButtonMap.entrySet()){
            Map.Entry pair = (Map.Entry) button;
            ((Button) pair.getValue()).setActive(false);
        }
        switch(currentCategory){
            case "All" :
                shownItems.addAll(items);
                break;

            default :
                for(Item x : items){
                    if(currentCategory.equalsIgnoreCase(x.getType())){
                        shownItems.add(x);
                    }
                }
        }
        if(!shownItems.isEmpty()){
            selectedItem = shownItems.get(0);
            updateItemShown();
        }
        int width = slotsWidth / itemsPerLine;
        for(int i = 0; i < shownItems.size(); i++){
            Button button = itemButtonMap.get(shownItems.get(i));
            button.setActive(true);
            button.setPosition(slotsXOffset + (i % itemsPerLine) * width,
                    slotsYOffset + (i / itemsPerLine) * width);
        }
    }

    @Override
    public void tick() {

    }

    private void drawGoldPanel(){
        if(goldPanelImageRenderer != null) goldPanelImageRenderer.render(0, 0);
        if(goldImageRenderer != null) goldImageRenderer.render(0, 0);
    }

    private void drawSlotsPanel(){
        if(slotsPanelImageRenderer != null) slotsPanelImageRenderer.render(0, 0);
        for(Item x : shownItems){
            Button button = itemButtonMap.get(x);
            button.render();
            if(selectedItem == x){
                MyGL2dRenderer.drawLabel(button.getX(), button.getY(), button.getWidth(), button.getHeight(), TextureData.selected_item_visual, 255);
            }
        }
    }

    private void drawInfoPanel(){
        if(infoPanelImageRenderer != null) infoPanelImageRenderer.render(0, 0);
        if(itemImage != null) itemImage.render(0, 0);
    }

    private void drawShopPanel(){
        shopPanelImageRenderer.render(0, 0);

        for(int i = 0; i < 9; i++){
            String character = Utils.getCharacterFromInt(i);
            if(selectedItem.getCharacters().contains(character)){
                stringImageMap.get(character).render(0, 0);
            }else{
                compatibleCharactersRenderers.get(i).render(0, 0);
            }
        }
    }

    private void drawBuyPanel(){
        buyPanelImageRenderer.render(0, 0);

        buyButton.render();
//        buyMoreCoins.render(canvas);
        priceImageRenderer.render(0, 0);
    }

    @Override
    public void render() {
//        paint.setColor(Color.argb(255, 200, 200, 0));
//        canvas.drawRect(GameView.getScreenRect(), paint);
//        paint.setColor(Color.WHITE);
//        canvas.drawRect(horOffset, verOffset, GameView.getScreenWidth() - horOffset, GameView.getScreenHeight() - verOffset, paint);
        drawGoldPanel();
        drawInfoPanel();
        drawSlotsPanel();
        drawBuyPanel();
        drawShopPanel();
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
