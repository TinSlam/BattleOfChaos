package com.tinslam.battleheart.states;

import android.graphics.Rect;
import android.opengl.GLES20;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.buttons.NullButton;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.HealthBarRenderer;
import com.tinslam.battleheart.UI.graphics.visualEffects.VisualEffect;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.entities.projectiles.Projectile;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses.Boss;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.Node;
import com.tinslam.battleheart.gameUtility.PathFindingMap;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.gameUtility.PositionOutsideCameraIndicator;
import com.tinslam.battleheart.gameUtility.SpawnCamp;
import com.tinslam.battleheart.gameUtility.StateAutomata;
import com.tinslam.battleheart.gameUtility.TouchHandler;
import com.tinslam.battleheart.spells.Spell;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.FileConsts;
import com.tinslam.battleheart.utils.constants.SpellConsts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class ReplayState extends BattleState{
    @SuppressWarnings("unchecked")
    private int mapWidth = 100, mapHeight = 100;
    @SuppressWarnings("unchecked")
    private ArrayList<Integer>[][] tiles = new ArrayList[mapWidth][mapHeight];
    public PathFindingMap pathFindingMap = new PathFindingMap();
    public int tileCellWidth = (int) (32 * GameView.density());
    public int tileCellHeight = (int) (32 * GameView.density());
    @SuppressWarnings("FieldCanBeLocal")
    private int tiles1Start = 0, tiles2Start = 136, tiles3Start = 336, tiles4Start = 600, tiles5Start = 744, tiles6Start = 896, tiles7Start = 1120;
    private Rect cameraRect = new Rect();
    private Rect cameraLockArea = new Rect();
    private boolean lockCamera = false;
    private ArrayList<float[]> allySpawnPoints = new ArrayList<>();
//    private boolean showingMessage = false;
    //    private Animation weatherAnimation;
    private boolean followingDisabled = false, lastFollowState;

    private final Object positionIndicatorsLock = new Object();
    private ArrayList<PositionOutsideCameraIndicator> positionIndicators = new ArrayList<>();

    private int[] vboId1 = new int[1];
    private int[] vboId2 = new int[1];
    private int[] iboId1 = new int[1];
    private int[] iboId2 = new int[1];
    private boolean vboCreated = false;

    private boolean cutScene = false;
    private int cutSceneAlpha = 0;
    private int cutSceneCounter = 0;

    private boolean shakeScreen = false;
    private int shakeCounter = 0;
    private int shakeFrames = 1;

    private int transitionCounter = 0;
    private int transitionFrames = Integer.MAX_VALUE;

    @Override
    public void onBackPressed() {
        if(GameView.stateChangeOnCD) return;
        State state = new DungeonSelectorState();
        GameView.setState(state, "");
        ActivityManager.switchToActivity(OpenGL2dActivity.openGL2dActivity, OpenGLActivity.class);
    }

    @Override
    public void lose(){
        // If this object is not the current arena state obsolete it.
        if(this != getReplayState()) return;

        battleStateLose();
    }

    @Override
    public void pcDied(Pc pc) {
        disablePcPortrait(pc);
    }

    @Override
    public void initBackground() {

    }

    private void initAutomata(){
        StateAutomata.clear();
        try {
            Scanner sc = new Scanner(GameView.Context().openFileInput("replay.txt"));
            ArrayList<StateAutomata> states = new ArrayList<>();
            while(sc.hasNext()){
                String line = sc.nextLine();
                if(line.startsWith("state")){
                    String line2;
                    ArrayList<String> events = new ArrayList<>();
                    do{
                        line2 = sc.nextLine();
                        events.add(line2);
                    }while(!line2.equalsIgnoreCase("over"));
                    states.add(new StateAutomata(events));
                }
            }
            if(states.isEmpty()){
                onBackPressed();
                return;
            }
            StateAutomata.setStates(states);
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
            GameView.setState(new DashboardState(), null);
            Utils.makeToast(R.string.no_replay_available);
            return;
        }
        StateAutomata.transition();
    }

    /**
     * Initializes the state.
     */
    @Override
    public void startBattleState() {
        LoadingState loadingState = new LoadingState(this);
        cameraRect = new Rect(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight());
//        weatherAnimation = new PerpetualAnimation(Utils.resizeAnimation(AnimationLoader.rain_2, GameView.getScreenWidth(), GameView.getScreenHeight()), 50, null);
//        weatherAnimation.reset();
        initMap();
        initAutomata();
        loadPcs();
        loadingState.finishLoading();
    }

    @Override
    public void loadSpells(){
        int i = 0;
        for(final Spell x : getSpells()){
            int xOffset = (int) (16 * GameView.density());
            int yOffset = (int) (12 * GameView.density());
            int gap = (int) (8 * GameView.density());
            int width = SpellConsts.PORTRAIT_WIDTH;
            RectangleButton rectangleButton = new RectangleButton(xOffset + i * (width + gap), yOffset,
                    x.getPortrait(), x.getPortrait(), x.getName(), true) {
                @Override
                public boolean onDown() {
                    return true;
                }

                @Override
                public boolean onUp() {
                    if(!x.isReady()) return true;
                    if(x.isTargeting()){
                        setSpellCasterPortrait(null);
                        x.onClick();
                    }else{
                        x.cast();
                    }
                    return true;
                }
            };
            rectangleButton.setActive(false);
            rectangleButton.getTextRenderer().show();
            x.setCancelArea(new Rect(rectangleButton.getX(), rectangleButton.getY(), rectangleButton.getX() + rectangleButton.getWidth(), rectangleButton.getY() + rectangleButton.getHeight()));
            getSpellButtons().add(rectangleButton);
            i++;
        }
    }

    private void setTextSizeDynamically(TextView tv, int x, int y, int maxWidth, int height){
        if(tv == null || tv.getPaint() == null) return;
        tv.measure(0, 0);
        int width;
        float size = height / 3 / GameView.density();
        int i = 0;
        Rect bounds = new Rect();
        do{
            tv.setTextSize(size - 0.5f * i);
            tv.getPaint().getTextBounds(tv.getText().toString(), 0, tv.getText().toString().length(), bounds);
            width = bounds.width();
            i++;
        }while(width > maxWidth);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
        params.leftMargin = x;
        params.topMargin = (int) (y - bounds.height() + tv.getPaint().descent() / 2);
        params.width = maxWidth;
        tv.setLayoutParams(params);
    }

    private void initMap(){
        try {
            InputStreamReader isr = new InputStreamReader(GameView.Context().getAssets().open("dungeon1_lowest_level"));
            BufferedReader br = new BufferedReader(isr);

            String line;
            String[] tokens;
            for(int j = 0; j < mapHeight; j++){
                line = br.readLine();
                tokens = line.split(",");
                for(int i = 0; i < mapWidth; i++){
                    tiles[i][j] = new ArrayList<>();
                    tiles[i][j].add(Integer.parseInt(tokens[i]) - 1);
                }
            }

            br.close();

            isr = new InputStreamReader(GameView.Context().getAssets().open("dungeon1_tile_layer1"));
            br = new BufferedReader(isr);

            for(int j = 0; j < mapHeight; j++){
                line = br.readLine();
                tokens = line.split(",");
                for(int i = 0; i < mapWidth; i++){
                    tiles[i][j].add(Integer.parseInt(tokens[i]) - 1);
                }
            }

            br.close();

            isr = new InputStreamReader(GameView.Context().getAssets().open("dungeon1_walkable"));
            br = new BufferedReader(isr);

            for(int j = 0; j < mapHeight; j++){
                line = br.readLine();
                tokens = line.split(",");
                for(int i = 0; i < mapWidth; i++){
                    if(Integer.parseInt(tokens[i]) - 1 != -1){
                        pathFindingMap.addWalkable(i, j);
                    }
                }
            }

            br.close();

            isr = new InputStreamReader(GameView.Context().getAssets().open("dungeon1_units_layer"));
            br = new BufferedReader(isr);

            line = br.readLine();
            while(line != null && !line.isEmpty()){
                if(line.startsWith("<object")){
                    tokens = line.split(" ");
                    String type = "";
                    float x = 0, y = 0;
                    for(int i = 1; i < tokens.length; i++){
                        if(tokens[i].startsWith("name=")) continue;
                        String[] tempTokens = tokens[i].split("=");
                        tempTokens[1] = tempTokens[1].substring(1, tempTokens[1].lastIndexOf('"'));
                        switch(tempTokens[0]){
                            case "type" :
                                type = tempTokens[1];
                                break;

                            case "x" :
                                x = Float.parseFloat(tempTokens[1]) * GameView.density();
                                break;

                            case "y" :
                                y = Float.parseFloat(tempTokens[1]) * GameView.density();
                                break;
                        }
                    }

                    int xTile = (int) (x / 32 / GameView.density());
                    int yTile = (int) (y / 32 / GameView.density());
                    if(type.startsWith("remove")){
                        String[] typeTokens = type.split("/");
                        if(PlayerStats.getLastLevelUnlocked() > Integer.parseInt(typeTokens[2])){
                            switch(typeTokens[1]){
                                case "tile_layer1" :
                                    tiles[xTile][yTile].set(1, -1);
                                    break;
                            }
                            if(typeTokens[3].equalsIgnoreCase("walkable")){
                                pathFindingMap.addWalkable(xTile, yTile);
                            }
                        }
                    }else if(type.startsWith("replace")){
                        String[] typeTokens = type.split("/");
                        int level = Integer.parseInt(typeTokens[4]);
                        if(PlayerStats.getLastLevelUnlocked() > level){
                            int index = Integer.parseInt(typeTokens[2]);
                            switch(typeTokens[3]){
                                case "tileset1" :
                                    index += tiles1Start;
                                    break;

                                case "tileset2" :
                                    index += tiles2Start;
                                    break;

                                case "tileset3" :
                                    index += tiles3Start;
                                    break;

                                case "tileset4" :
                                    index += tiles4Start;
                                    break;

                                case "tileset5" :
                                    index += tiles5Start;
                                    break;

                                case "tileset6" :
                                    index += tiles6Start;
                                    break;

                                case "tileset7" :
                                    index += tiles7Start;
                                    break;
                            }
                            int layerIndex = 0;
                            switch(typeTokens[1]){
                                case "tile_layer1" :
                                    layerIndex = 1;
                                    break;
                            }
                            tiles[xTile][yTile].set(layerIndex, index);
                            if(typeTokens[5].equalsIgnoreCase("walkable")) pathFindingMap.addWalkable(xTile, yTile);
                        }
                    }

                    switch(type){
                        case "ally_spawn_point" :
                            allySpawnPoints.add(new float[] {x, y});
                            break;
                    }
                }
                try{
                    line = br.readLine();
                }catch(Exception e){
                    line = "";
                    e.printStackTrace();
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        pathFindingMap.updateClearance();
        getMapRect().set(0, -getMapTop(), mapWidth * tileCellWidth, mapHeight * tileCellHeight);
        SpawnCamp.spawnCamps();
    }

    public void bossDone(){
        lockCamera = false;
        setFollowingDisabled(lastFollowState);
        if(getSelectedPc() != null){ // This will enable following.
            setSelectedPc(getSelectedPc());
        }
    }

    public void triggerBoss(Boss boss){
        cutScene = true;
        cutSceneAlpha = 0;
        cutSceneCounter = 0;
        lockCamera = true;
        cameraLockArea = boss.getBossArea();
        cameraX = -boss.getBossArea().left;
        cameraY = -boss.getBossArea().top;
        cameraRect.set((int) -cameraX, (int) -cameraY, (int) -cameraX + cameraRect.width(), (int) -cameraY + cameraRect.height());
        lastFollowState = isFollowingDisabled();
        setFollowingDisabled(true);
        for(Pc pc : Pc.getPcs()){
            pc.resetCommands();
            if(pc == getSelectedPc()) continue;
            pc.setFollow(false, null, 0);
            int x, y;
            int spawnRadius = GameView.getScreenHeight() / 2;
            do{
                x = Utils.getRandomIntegerInTheRange((int) (getSelectedPc().getX() - spawnRadius), (int) (getSelectedPc().getX() + spawnRadius), null);
                y = Utils.getRandomIntegerInTheRange((int) (getSelectedPc().getY() - spawnRadius), (int) (getSelectedPc().getY() + spawnRadius), null);
            }while(!ReplayState.getReplayState().canMove(new Rect(x, y + pc.getCollisionBox().height() - pc.getSolidBox().height(), x + pc.getSolidBox().width(), y + pc.getCollisionBox().height()), pc.getXClearance(), pc.getYClearance()));
            pc.teleport(x, y);
        }
    }

    private void disableFollowing(){
        for(Pc pc : Pc.getPcs()){
            if(pc == getSelectedPc()) continue;
            pc.setFollow(false, null, 0);
        }
    }

    private void loadPcs(){
        new Event() {
            @Override
            public void performAction() {
                setSelectedPc(Pc.getPcs().get(0));
                cameraX = -getSelectedPc().getX() - getSelectedPc().getCollisionBox().width() / 2 + GameView.getScreenWidth() / 2;
                cameraY = -getSelectedPc().getY() - getSelectedPc().getCollisionBox().height() / 2 + GameView.getScreenHeight() / 2;
                cameraRect.set((int) -cameraX, (int) -cameraY, (int) -cameraX + cameraRect.width(), (int) -cameraY + cameraRect.height());

                for(final Pc x : Pc.getPcs()){
                    positionIndicators.add(new PositionOutsideCameraIndicator(x));
                }
            }
        };
    }

    @Override
    public void battleStateWin() {

    }

    @Override
    public void battleStateLose() {

    }

    @Override
    public void tickBattleState() {
        if(!vboCreated){
            vboCreated = true;
            createStaticVBOAndIBO();
        }
        if(!lockCamera){
            if(getSelectedPc() != null){
                cameraX = ((-getSelectedPc().getX() - getSelectedPc().getCollisionBox().width() / 2 + GameView.getScreenWidth() / 2) + cameraX) / 2;
                cameraY = ((-getSelectedPc().getY() - getSelectedPc().getCollisionBox().height() / 2 + GameView.getScreenHeight() / 2) + cameraY) / 2;
            }
            cameraRect.set((int) -cameraX, (int) -cameraY, (int) -cameraX + cameraRect.width(), (int) -cameraY + cameraRect.height());
        }else if(getSelectedPc() != null){
            float x = (-getSelectedPc().getX() - getSelectedPc().getCollisionBox().width() / 2 + GameView.getScreenWidth() / 2);
            float y = (-getSelectedPc().getY() - getSelectedPc().getCollisionBox().height() / 2 + GameView.getScreenHeight() / 2);
            cameraRect.left = Utils.min(cameraLockArea.right - GameView.getScreenWidth(), Utils.max((int) -x, cameraLockArea.left));
            cameraRect.top = Utils.min(cameraLockArea.bottom - GameView.getScreenHeight(), Utils.max((int) -y, cameraLockArea.top));
            cameraRect.right = cameraRect.left + GameView.getScreenWidth();
            cameraRect.bottom = cameraRect.top + GameView.getScreenHeight();
            cameraX = -cameraRect.left;
            cameraY = -cameraRect.top;
        }
        if(shakeScreen){
            shakeCounter++;
            if(shakeCounter % 2 == 1){
                cameraX += Utils.getRandomIntegerInTheRange((int) (-4 * GameView.density()), (int) (4 * GameView.density()), null);
                cameraY += Utils.getRandomIntegerInTheRange((int) (-4 * GameView.density()), (int) (4 * GameView.density()), null);
            }
            if(shakeCounter == shakeFrames){
                shakeScreen = false;
            }
        }
    }

    @Override
    public void renderBackground() {
        int w;
        int h;
        w = GameView.getScreenWidth() / 2;
        h = GameView.getScreenHeight() / 2;
        if(iboId1[0] != 0 && vboId1[0] != 0) MyGL2dRenderer.drawLabel(cameraX + GameView.getScreenWidth() / 2, cameraY + GameView.getScreenHeight(), w, -h, TextureData.atlas_0, 255,
                vboId1[0], iboId1[0], mapWidth * mapHeight * 6, 0);
        if(iboId2[0] != 0 && vboId2[0] != 0) MyGL2dRenderer.drawLabel(cameraX + GameView.getScreenWidth() / 2, cameraY + GameView.getScreenHeight(), w, -h, TextureData.atlas_0, 255,
                vboId2[0], iboId2[0], mapWidth * mapHeight * 6, 0);
    }

    @Override
    public void tickState(){
//        if(tickSlowMo()) return;
        Animation.tick();
        tick();
        for(Entity x : Entity.getEntities()){
//                if(Utils.distance(cameraRect.centerX(), cameraRect.centerY(), x.getX(), x.getY()) < cameraRect.width() || x instanceof Pc || x instanceof Projectile || x instanceof Boss){
            x.tick();
//                }
        }
        VisualEffect.tick();
        transitionCounter++;
        if(transitionCounter == transitionFrames){
            StateAutomata.transition();
        }
    }

    @Override
    public void renderState(){
        renderBackground();
        TouchHandler.render(cameraX, cameraY);
        if(getSelectedPc() != null){
            MyGL2dRenderer.drawLabel(getSelectedPc().getCollisionBox().left - 4 * GameView.density() + cameraX,
                    getSelectedPc().getCollisionBox().bottom - getSelectedPc().getCollisionBox().width() / 5 + cameraY,
                    getSelectedPc().getCollisionBox().width() + 8 * GameView.density(),
                    getSelectedPc().getCollisionBox().width() * 2 / 5,
                    TextureData.selected_character_visual, 255);
        }
        ArrayList<Entity> entitiesToBeDrawn = new ArrayList<>();
        ArrayList<HealthBarRenderer> healthBars = new ArrayList<>();
        for(Entity x : Entity.getEntities()){
            if(cameraRect.intersects(x.getCollisionBox().left, x.getCollisionBox().top, x.getCollisionBox().right, x.getCollisionBox().bottom) ||
                    (x instanceof Boss && ((Boss) x).isTriggered()) || x instanceof Projectile){
                entitiesToBeDrawn.add(x);
                if(x instanceof Unit){
                    HealthBarRenderer healthBarRenderer = ((Unit) x).getHealthBar();
                    if(healthBarRenderer != null && ((Unit) x).getShowHealthBar()) healthBars.add(healthBarRenderer);
                }
            }
        }
        sortEntities(entitiesToBeDrawn);
        for(Entity x : entitiesToBeDrawn){
            x.render(cameraX, cameraY);
        }
        for(HealthBarRenderer hb : healthBars){
            hb.renderHealthBar(cameraX, cameraY);
        }
        for(final PositionOutsideCameraIndicator indicator : positionIndicators){
            Unit x = indicator.getUnit();
            if(x == null || !x.doesExist()){
                new Event() {
                    @Override
                    public void performAction() {
                        positionIndicators.remove(indicator);
                    }
                };
                continue;
            }
            if(getSelectedPc() != x){
                if(!cameraRect.intersects(x.getCollisionBox().left, x.getCollisionBox().top, x.getCollisionBox().right, x.getCollisionBox().bottom)){
                    indicator.render(-cameraX + GameView.getScreenWidth() / 2, -cameraY + GameView.getScreenHeight() / 2);
                }
            }
        }
        Button.renderButtons(getButtons(), getButtonsLock());
        renderSpells();
        for(VisualEffect x : VisualEffect.getVisualEffects()){
            Animation animation = x.getAnimation();
            if(cameraRect.intersects((int) x.getX(), (int) x.getY(), (int) x.getX() + animation.getWidth(), (int) (x.getY() + animation.getHeight()))){
                x.renderVisualEffect(cameraX, cameraY);
            }
        }
        if(fpsTextRenderer != null) fpsTextRenderer.setText("Average FPS : " + GameThread.avgFps);
        renderOver();
        if(confirmation){
            drawConfirmation();
        }
//        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.solid_dark_blue, 92);
//        weatherAnimation.render(0, 0);
        drawLoading();
        renderCutScene();
    }

    private void renderCutScene(){
        if(cutScene){
            if(cutSceneCounter < 255 / 12){
                cutSceneAlpha += 12;
            }else if(cutSceneCounter >= 600 / 12){
                cutSceneAlpha -= 12;
                if(cutSceneAlpha == 0) cutScene = false;
            }
            cutSceneCounter++;
            MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight() / 6, TextureData.solid_black, cutSceneAlpha);
            MyGL2dRenderer.drawLabel(0, GameView.getScreenHeight() * 5 / 6, GameView.getScreenWidth(), GameView.getScreenHeight() / 6, TextureData.solid_black, cutSceneAlpha);
        }
    }

    @Override
    public void renderBattleState() {

    }

    @Override
    public void renderOverBattleState() {

    }

    /**
     * Checks whether an area on the map is possible to move to.
     */
    public boolean canMove(Rect src, int xClearance, int yClearance) {
//        if(lockCamera){
//            if(!cameraRect.contains(src.left, src.top, src.right, src.bottom)) return false;
//        }
        Rect rect = new Rect(src.left, src.top, src.left + src.width() / xClearance, src.top + src.height() / yClearance);
        try{
            ArrayList<int[]> tilesIntersected = getTilesIntersected(rect);
            for(int i = 0; i < tilesIntersected.size(); i++){
                Node node = pathFindingMap.get(tilesIntersected.get(i)[0], tilesIntersected.get(i)[1]);
                if(node == null || node.xClearance < xClearance || node.yClearance < yClearance){
                    return false;
                }
            }
        }catch(Exception e){
            return false;
        }

        return true;
    }

    private ArrayList<int[]> getTilesIntersected(Rect rect){
        ArrayList<int[]> list = new ArrayList<>();

        int x1 = rect.left / tileCellWidth;
        int y1 = rect.top / tileCellHeight;
        int x2 = rect.right / tileCellWidth;
        int y2 = rect.bottom / tileCellHeight;



        for(int i = x1; i <= x2; i++){
            for(int j = y1; j <= y2; j++){
                list.add(new int[] {i, j});
            }
        }

        return list;
    }

    public boolean onUp(int x, int y){
        return true;
    }

    /**
     * Checks whether an area on the map is out of bounds.
     */
    @Override
    public boolean isOutOfBounds(int x, int y) {
        return false;
    }

    /**
     * This method is called when the game view loses focus.
     */
    @Override
    public void surfaceDestroyed() {

    }

    /**
     * Listener for handling the key events.
     *
     * @param event The key event.
     */
    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    private void createStaticVBOAndIBO(){
        try{
            GLES20.glDeleteBuffers(1, vboId1, 0);
            GLES20.glDeleteBuffers(1, vboId2, 0);
            GLES20.glDeleteBuffers(1, iboId1, 0);
            GLES20.glDeleteBuffers(1, iboId2, 0);
            createLayer(0, vboId1, iboId1);
            createLayer(1, vboId2, iboId2);
        }catch(Exception ignored){
            vboCreated = false;
        }
    }

    private void createLayer(int layer, int[] vboId, int[] iboId){
        float[] vertices = new float[mapWidth * mapHeight * 4 * 5];
        short[] indices = new short[mapWidth * mapHeight * 6];
        for(int i = 0; i < mapWidth; i++){
            for(int j = 0; j < mapHeight; j++){
                if(tiles[i][j].get(layer) == -1) continue;
                indices[6 * (i * mapHeight + j)] = (short) (4 * (i * mapHeight + j));
                indices[6 * (i * mapHeight + j) + 1] = (short) (4 * (i * mapHeight + j) + 2);
                indices[6 * (i * mapHeight + j) + 2] = (short) (4 * (i * mapHeight + j) + 1);
                indices[6 * (i * mapHeight + j) + 3] = (short) (4 * (i * mapHeight + j) + 1);
                indices[6 * (i * mapHeight + j) + 4] = (short) (4 * (i * mapHeight + j) + 2);
                indices[6 * (i * mapHeight + j) + 5] = (short) (4 * (i * mapHeight + j) + 3);

                vertices[5 * 4 * (i * mapHeight + j)] = MyGL2dRenderer.toOpenGLCoordsX(i * tileCellWidth);
                vertices[5 * 4 * (i * mapHeight + j) + 1] = MyGL2dRenderer.toOpenGLCoordsY(j * tileCellHeight);
                vertices[5 * 4 * (i * mapHeight + j) + 2] = 0;
                vertices[5 * 4 * (i * mapHeight + j) + 3] = getZeroXTextureCoord(tiles[i][j].get(layer));
                vertices[5 * 4 * (i * mapHeight + j) + 4] = getOneYTextureCoord(tiles[i][j].get(layer));

                vertices[5 * 4 * (i * mapHeight + j) + 5] = MyGL2dRenderer.toOpenGLCoordsX(i * tileCellWidth);
                vertices[5 * 4 * (i * mapHeight + j) + 6] = MyGL2dRenderer.toOpenGLCoordsY(j * tileCellHeight + tileCellHeight);
                vertices[5 * 4 * (i * mapHeight + j) + 7] = 0;
                vertices[5 * 4 * (i * mapHeight + j) + 8] = getZeroXTextureCoord(tiles[i][j].get(layer));
                vertices[5 * 4 * (i * mapHeight + j) + 9] = getZeroYTextureCoord(tiles[i][j].get(layer));

                vertices[5 * 4 * (i * mapHeight + j) + 10] = MyGL2dRenderer.toOpenGLCoordsX(i * tileCellWidth + tileCellWidth);
                vertices[5 * 4 * (i * mapHeight + j) + 11] = MyGL2dRenderer.toOpenGLCoordsY(j * tileCellHeight);
                vertices[5 * 4 * (i * mapHeight + j) + 12] = 0;
                vertices[5 * 4 * (i * mapHeight + j) + 13] = getOneXTextureCoord(tiles[i][j].get(layer));
                vertices[5 * 4 * (i * mapHeight + j) + 14] = getOneYTextureCoord(tiles[i][j].get(layer));

                vertices[5 * 4 * (i * mapHeight + j) + 15] = MyGL2dRenderer.toOpenGLCoordsX(i * tileCellWidth + tileCellWidth);
                vertices[5 * 4 * (i * mapHeight + j) + 16] = MyGL2dRenderer.toOpenGLCoordsY(j * tileCellHeight + tileCellHeight);
                vertices[5 * 4 * (i * mapHeight + j) + 17] = 0;
                vertices[5 * 4 * (i * mapHeight + j) + 18] = getOneXTextureCoord(tiles[i][j].get(layer));
                vertices[5 * 4 * (i * mapHeight + j) + 19] = getZeroYTextureCoord(tiles[i][j].get(layer));
            }
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * MyGLRenderer.BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vbo = bb.asFloatBuffer();
        vbo.put(vertices);
        vbo.position(0);

        GLES20.glGenBuffers(1, vboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vbo.capacity() * MyGLRenderer.BYTES_PER_FLOAT,
                vbo, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * MyGLRenderer.BYTES_PER_SHORT);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer ibo = dlb.asShortBuffer();
        ibo.put(indices);
        ibo.position(0);

        GLES20.glGenBuffers(1, iboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboId[0]);

        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo.capacity() * MyGLRenderer.BYTES_PER_SHORT,
                ibo, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private float getZeroXTextureCoord(int data){
        float xOffset;
        float maxWidth = 1024;
        if(data < tiles2Start){
            xOffset = 0;
        }else if(data < tiles3Start){
            xOffset = 768;
            data -= tiles2Start;
        }else if(data < tiles4Start){
            xOffset = 256;
            data -= tiles3Start;
        }else if(data < tiles5Start){
            xOffset = 256;
            data -= tiles4Start;
        }else if(data < tiles6Start){
            xOffset = 512;
            data -= tiles5Start;
        }else if(data < tiles7Start){
            xOffset = 0;
            data -= tiles6Start;
        }else{
            xOffset = 512;
            data -= tiles7Start;
        }
        data %= 8;
        data *= 32;
        return (xOffset + data + 0.5f) / maxWidth;
    }

    private float getOneXTextureCoord(int data){
        float xOffset;
        float maxWidth = 1024;
        if(data < tiles2Start){
            xOffset = 0;
        }else if(data < tiles3Start){
            xOffset = 768;
            data -= tiles2Start;
        }else if(data < tiles4Start){
            xOffset = 256;
            data -= tiles3Start;
        }else if(data < tiles5Start){
            xOffset = 256;
            data -= tiles4Start;
        }else if(data < tiles6Start){
            xOffset = 512;
            data -= tiles5Start;
        }else if(data < tiles7Start){
            xOffset = 0;
            data -= tiles6Start;
        }else{
            xOffset = 512;
            data -= tiles7Start;
        }
        data %= 8;
        data++;
        data *= 32;
        return (xOffset + data - 0.5f) / maxWidth;
    }

    private float getOneYTextureCoord(int data){
        float yOffset;
        float maxHeight = 1856;
        if(data < tiles2Start){
            yOffset = 0;
        }else if(data < tiles3Start){
            yOffset = 0;
            data -= tiles2Start;
        }else if(data < tiles4Start){
            yOffset = 800;
            data -= tiles3Start;
        }else if(data < tiles5Start){
            yOffset = 0;
            data -= tiles4Start;
        }else if(data < tiles6Start){
            yOffset = 0;
            data -= tiles5Start;
        }else if(data < tiles7Start){
            yOffset = 800;
            data -= tiles6Start;
        }else{
            yOffset = 800;
            data -= tiles7Start;
        }
        data /= 8;
        data *= 32;
        return (yOffset + data + 0.5f) / maxHeight;
    }

    private float getZeroYTextureCoord(int data){
        float yOffset;
        float maxHeight = 1856;
        if(data < tiles2Start){
            yOffset = 0;
        }else if(data < tiles3Start){
            yOffset = 0;
            data -= tiles2Start;
        }else if(data < tiles4Start){
            yOffset = 800;
            data -= tiles3Start;
        }else if(data < tiles5Start){
            yOffset = 0;
            data -= tiles4Start;
        }else if(data < tiles6Start){
            yOffset = 0;
            data -= tiles5Start;
        }else if(data < tiles7Start){
            yOffset = 800;
            data -= tiles6Start;
        }else{
            yOffset = 800;
            data -= tiles7Start;
        }
        data /= 8;
        data++;
        data *= 32;
        return (yOffset + data - 0.5f) / maxHeight;
    }

    private void shakeScreen(int frameCount){
        shakeFrames = frameCount;
        shakeScreen = true;
        shakeCounter = 0;
    }

    public void shakeScreen(){
        shakeScreen(Integer.MAX_VALUE);
    }

    public void stopScreenShaking(){
        shakeScreen = false;
    }

    public static ReplayState getReplayState(){
        return (ReplayState) BattleState.getBattleState();
    }

    private void setFollowingDisabled(boolean flag){
        followingDisabled = flag;
        if(flag) disableFollowing();
        else if(getSelectedPc() != null) setSelectedPc(getSelectedPc());
    }

    boolean isFollowingDisabled() {
        return followingDisabled;
    }

    @Override
    public boolean onActionUp(MotionEvent event){
        return true;
    }

    @Override
    public boolean onActionPointerUp(MotionEvent event){
        return true;
    }

    @Override
    public boolean onActionMove(MotionEvent event){
        return true;
    }

    @Override
    public boolean onActionDown(MotionEvent event){
        return true;
    }

    @Override
    public boolean onActionPointerDown(MotionEvent event){
        return true;
    }

    public void setTransitionFrames(int frames) {
        transitionFrames = frames;
        transitionCounter = 0;
    }
}
