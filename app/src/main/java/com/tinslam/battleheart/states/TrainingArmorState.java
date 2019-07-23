package com.tinslam.battleheart.states;

import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.view.KeyEvent;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.HealthBarRenderer;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.ImageRenderer;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.UI.graphics.visualEffects.VisualEffect;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.GreenGoblin;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.MummyArcher;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.Troll;
import com.tinslam.battleheart.entities.units.NPCs.Npc;
import com.tinslam.battleheart.entities.units.PCs.Archer;
import com.tinslam.battleheart.entities.units.PCs.Knight;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.PCs.Priest;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.gameUtility.TouchHandler;
import com.tinslam.battleheart.spells.Spell;
import com.tinslam.battleheart.utils.DistanceMeasurer;
import com.tinslam.battleheart.utils.FileManager;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.Timer;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.FileConsts;
import com.tinslam.battleheart.utils.constants.NameConsts;
import com.tinslam.battleheart.utils.constants.SpellConsts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class TrainingArmorState extends BattleState{
    private String character;
    private Pc player;
    private static float tilesCellWidth, tilesCellHeight;
    private static byte[][] tiles;
    private static int tilesWidth, tilesHeight;
    private int spawnCounter = 0, spawnInterval = 120;
    private float damage = 10, speed = 3;
    private Rect cameraRect;
    private Timer timer;
    private DistanceMeasurer distanceMeasurer;
    private boolean endDelayed = false;
    private float spawnerX = 0, spawnerY = 0;

    private static int infoPanelWidth = GameView.getScreenWidth() / 5;
    private static int infoPanelHeight = GameView.getScreenHeight() / 6;
    private static int infoPanelXOffset = (int) (GameView.getScreenWidth() - infoPanelWidth - 8 * GameView.density());
    private static int infoPanelYOffset = (int) (8 * GameView.density());

    private static int timerImageHeight = infoPanelHeight / 3;
    private static int timerImageWidth = timerImageHeight;
    private static int timerImageXOffset = infoPanelXOffset + infoPanelWidth / 10;
    private static int timerImageYOffset = infoPanelYOffset + (infoPanelHeight / 2 - timerImageHeight) / 2;
    private static int timerXOffset = timerImageXOffset + timerImageWidth + infoPanelWidth / 10;
    private static int timerYOffset = timerImageYOffset + timerImageHeight / 2;
    private static int timerWidth = GameView.getScreenWidth() - infoPanelWidth / 10 - timerXOffset;
    private static int timerHeight = timerImageHeight;
    private static int distanceXOffset = timerXOffset;
    private static int distanceWidth = timerWidth;
    private static int distanceHeight = timerHeight;
    private static int distanceImageXOffset = timerImageXOffset;
    private static int distanceImageWidth = timerImageWidth;
    private static int distanceImageHeight = timerImageHeight;
    private static int distanceImageYOffset = infoPanelYOffset + infoPanelHeight / 2 + (infoPanelHeight / 2 - distanceImageHeight) / 2;;
    private static int distanceYOffset = distanceImageYOffset + distanceImageHeight / 2;
    private static int endPanelWidth = GameView.getScreenWidth() - 2 * GameView.getScreenWidth() / 5;
    private static int endPanelHeight = GameView.getScreenHeight() - 2 * GameView.getScreenHeight() / 7;
    private static int endPanelXOffset = (GameView.getScreenWidth() - endPanelWidth) / 2;
    private static int endPanelYOffset = (GameView.getScreenHeight() - endPanelHeight) / 2;
    private int xEndPosition = infoPanelXOffset;
    private int yEndPosition = infoPanelYOffset;
    private int widthEnd = infoPanelWidth;
    private int heightEnd = infoPanelHeight;
    private static int scaleFrames = 12;
    private boolean end = false, done = false;

    private ImageRenderer infoPanelImageRenderer, timerImageRenderer, distanceImageRenderer;
    private TextRenderer timerRenderer, distanceRenderer, bonusMsRenderer, bonusMsText;

    private boolean vboCreated = false;
    private int[] vboId = new int[1];
    private int[] iboId = new int[1];

    static{
        tiles = FileManager.loadFile(FileConsts.TRAINING_ARMOR_FILE_NAME, 20, 20);
        if(tiles == null){
            ActivityManager.closeGame();
        }
        tilesWidth = 20;
        tilesHeight = 20;
        tilesCellWidth = 64 * GameView.density();
        tilesCellHeight = 64 * GameView.density();
    }

    TrainingArmorState(String character){
        this.character = character;
    }

    private void createStaticVBOAndIBO() {
        GLES20.glDeleteBuffers(1, vboId, 0);
        GLES20.glDeleteBuffers(1, iboId, 0);
        float[] vertices = new float[tilesWidth * tilesHeight * 4 * 5];
        short[] indices = new short[tilesWidth * tilesHeight * 6];
        for(int i = 0; i < tilesWidth; i++){
            for(int j = 0; j < tilesHeight; j++){
                indices[6 * (i * tilesHeight + j)] = (short) (4 * (i * tilesHeight + j));
                indices[6 * (i * tilesHeight + j) + 1] = (short) (4 * (i * tilesHeight + j) + 2);
                indices[6 * (i * tilesHeight + j) + 2] = (short) (4 * (i * tilesHeight + j) + 1);
                indices[6 * (i * tilesHeight + j) + 3] = (short) (4 * (i * tilesHeight + j) + 1);
                indices[6 * (i * tilesHeight + j) + 4] = (short) (4 * (i * tilesHeight + j) + 2);
                indices[6 * (i * tilesHeight + j) + 5] = (short) (4 * (i * tilesHeight + j) + 3);

                vertices[5 * 4 * (i * tilesHeight + j)] = MyGL2dRenderer.toOpenGLCoordsX(i * tilesCellWidth);
                vertices[5 * 4 * (i * tilesHeight + j) + 1] = MyGL2dRenderer.toOpenGLCoordsY(j * tilesCellHeight);
                vertices[5 * 4 * (i * tilesHeight + j) + 2] = 0;
                switch(tiles[i][j]){ // 0 1
                    case 0 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 3] = 0;
                        vertices[5 * 4 * (i * tilesHeight + j) + 4] = 1;
                        break;

                    case 1 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 3] = 0;
                        vertices[5 * 4 * (i * tilesHeight + j) + 4] = 99f / 310;
                        break;

                    case 2 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 3] = 100f / 210;
                        vertices[5 * 4 * (i * tilesHeight + j) + 4] = 109f / 310;
                        break;
                }

                vertices[5 * 4 * (i * tilesHeight + j) + 5] = MyGL2dRenderer.toOpenGLCoordsX(i * tilesCellWidth);
                vertices[5 * 4 * (i * tilesHeight + j) + 6] = MyGL2dRenderer.toOpenGLCoordsY(j * tilesCellHeight + tilesCellHeight);
                vertices[5 * 4 * (i * tilesHeight + j) + 7] = 0;
                switch(tiles[i][j]){ // 0 0
                    case 0 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 8] = 0;
                        vertices[5 * 4 * (i * tilesHeight + j) + 9] = 110f / 310;
                        break;

                    case 1 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 8] = 0;
                        vertices[5 * 4 * (i * tilesHeight + j) + 9] = 0;
                        break;

                    case 2 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 8] = 100f / 210;
                        vertices[5 * 4 * (i * tilesHeight + j) + 9] = 0;
                        break;
                }

                vertices[5 * 4 * (i * tilesHeight + j) + 10] = MyGL2dRenderer.toOpenGLCoordsX(i * tilesCellWidth + tilesCellWidth);
                vertices[5 * 4 * (i * tilesHeight + j) + 11] = MyGL2dRenderer.toOpenGLCoordsY(j * tilesCellHeight);
                vertices[5 * 4 * (i * tilesHeight + j) + 12] = 0;
                switch(tiles[i][j]){ // 1 1
                    case 0 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 13] = 199f / 210;
                        vertices[5 * 4 * (i * tilesHeight + j) + 14] = 1;
                        break;

                    case 1 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 13] = 99f / 210;
                        vertices[5 * 4 * (i * tilesHeight + j) + 14] = 99f / 310;
                        break;

                    case 2 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 13] = 1;
                        vertices[5 * 4 * (i * tilesHeight + j) + 14] = 109f / 310;
                        break;
                }

                vertices[5 * 4 * (i * tilesHeight + j) + 15] = MyGL2dRenderer.toOpenGLCoordsX(i * tilesCellWidth + tilesCellWidth);
                vertices[5 * 4 * (i * tilesHeight + j) + 16] = MyGL2dRenderer.toOpenGLCoordsY(j * tilesCellHeight + tilesCellHeight);
                vertices[5 * 4 * (i * tilesHeight + j) + 17] = 0;
                switch(tiles[i][j]){ // 1 0
                    case 0 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 18] = 199f / 210;
                        vertices[5 * 4 * (i * tilesHeight + j) + 19] = 110f / 310;
                        break;

                    case 1 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 18] = 99f / 210;
                        vertices[5 * 4 * (i * tilesHeight + j) + 19] = 0;
                        break;

                    case 2 :
                        vertices[5 * 4 * (i * tilesHeight + j) + 18] = 1;
                        vertices[5 * 4 * (i * tilesHeight + j) + 19] = 0;
                        break;
                }
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

    @Override
    public void onBackPressed() {
        if(GameView.stateChangeOnCD) return;
        GameView.setState(new TrainingSelectorState(), "");
    }

    @Override
    public void pcDied(Pc pc) {

    }

    @Override
    public void initBackground() {
        boolean flag = false;
        for(int i = 0; i < tilesWidth; i++){
            for(int j = 0; j < tilesHeight; j++){
                if(tiles[i][j] == 2){
                    spawnerX = i * tilesCellWidth + cameraX + tilesCellWidth / 2;
                    spawnerY = j * tilesCellHeight + cameraY + tilesCellHeight / 2;
                    flag = true;
                    break;
                }
            }
            if(flag) break;
        }
        setMapTop(0);
        cameraRect = new Rect(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight());
        getMapRect().set(0, getMapTop(), (int) (tilesWidth * tilesCellWidth), (int) (tilesHeight * tilesCellHeight));
    }

    private void initInfo(){
        infoPanelImageRenderer = new ImageRenderer(infoPanelXOffset, infoPanelYOffset, infoPanelWidth, infoPanelHeight, TextureData.light_brown_panel);

        distanceImageRenderer = new ImageRenderer(distanceImageXOffset, distanceImageYOffset, distanceImageWidth, distanceImageHeight, TextureData.footprints);

        timerImageRenderer = new ImageRenderer(timerImageXOffset, timerImageYOffset, timerImageWidth, timerImageHeight, TextureData.hourglass);

        distanceRenderer = new TextRenderer("0",
                distanceXOffset, distanceYOffset, distanceWidth, distanceHeight, Paint.Align.LEFT, false, false);
        distanceRenderer.show();

        timerRenderer = new TextRenderer("0",
                timerXOffset, timerYOffset, timerWidth, timerHeight, Paint.Align.LEFT, false, false);
        timerRenderer.show();
    }

    private void updateInfoPanel(){
        if(distanceRenderer != null && distanceMeasurer != null) distanceRenderer.setText("" + distanceMeasurer.getDistanceAsString());
        if(timerRenderer != null && timer != null) timerRenderer.setText("" + timer.getTimeAsString());
    }

    /**
     * Initializes the state.
     */
    @Override
    public void startBattleState() {
        initInfo();
        int x = GameView.getScreenWidth() / 2;
        int y = GameView.getScreenHeight() / 2;
        switch(character) {
            case NameConsts.KNIGHT:
                player = new Knight(x, y);
                break;

            case NameConsts.PRIEST:
                player = new Priest(x, y);
                break;

            case NameConsts.ARCHER:
                player = new Archer(x, y);
                break;

            default:
                System.out.println("TrainingArmorState's startBattleState() method switch command default called for the character : " + character);
        }
        new Event() {
            @Override
            public void performAction() {
                setSelectedPc(player);
                cameraX = -getSelectedPc().getX() - getSelectedPc().getCollisionBox().width() / 2 + GameView.getScreenWidth() / 2;
                cameraY = -getSelectedPc().getY() - getSelectedPc().getCollisionBox().height() / 2 + GameView.getScreenHeight() / 2;
                cameraRect.set((int) -cameraX, (int) -cameraY, (int) -cameraX + cameraRect.width(), (int) -cameraY + cameraRect.height());
                player.loadSpells(getSpells());
                int i = 0;
                for(final Spell spell : getSpells()){
                    int xOffset = (int) (16 * GameView.density());
                    int yOffset = (int) (12 * GameView.density());
                    int gap = (int) (8 * GameView.density());
                    int width = SpellConsts.PORTRAIT_WIDTH;
                    RectangleButton button = new RectangleButton(xOffset + i * (width + gap), yOffset,
                            spell.getPortrait(), spell.getPortrait(), spell.getName(), true) {
                        @Override
                        public boolean onDown() {
                            return true;
                        }

                        @Override
                        public boolean onUp() {
                            if(!spell.isReady()) return true;
                            if(spell.isTargeting()){
                                setSpellCasterPortrait(null);
                                spell.onClick();
                            }else{
                                spell.cast();
                            }
                            return true;
                        }
                    };
                    button.getTextRenderer().show();
                    getSpellButtons().add(button);
                    i++;
                }
                distanceMeasurer = new DistanceMeasurer(player);
                distanceMeasurer.start();
                timer = new Timer();
                timer.start();
            }
        };
    }

    @Override
    public void battleStateWin() {

    }

    @Override
    public void battleStateLose() {
        done = true;
    }

    @Override
    public void tickBattleState() {
        if(!vboCreated){
            vboCreated = true;
            try{
                createStaticVBOAndIBO();
            }catch(Exception e){
                vboCreated = false;
            }
        }
        if(player != null){
            cameraX = -player.getX() - player.getCollisionBox().width() / 2 + GameView.getScreenWidth() / 2;
            cameraY = -player.getY() - player.getCollisionBox().height() / 2 + GameView.getScreenHeight() / 2;
            cameraX = (int) cameraX;
            cameraY = (int) cameraY;
        }
        if(cameraRect != null) cameraRect.set((int) -cameraX, (int) -cameraY, (int) -cameraX + cameraRect.width(), (int) -cameraY + cameraRect.height());
        if(done){
            if(!end){
                timer.stop();
                ended();
            }
            end = true;
            scalePanel();
        }else if(player != null){
            if(distanceMeasurer != null) distanceMeasurer.mesaure();
            updateInfoPanel();
            spawnCounter++;
            if(spawnCounter == spawnInterval){
                damage += 7;
                speed += 0.2f;
                if(Npc.getNpcs().size() >= 10){
                    damage *= 7f / 6;
                    speed += 1;
                    Npc.getNpcs().get(0).destroy();
                    for(Npc x : Npc.getNpcs()){
                        x.setDamage(damage);
                        x.setSpeed(speed);
                    }
                }
                spawnCounter = 0;
                final Npc npc;
                float random = (float) Math.random();
                if(random < 0.33){
                    npc = new MummyArcher(spawnerX, spawnerY);
                }else if(random < 0.66){
                    npc = new Troll(spawnerX, spawnerY);

                }else{
                    npc = new GreenGoblin(spawnerX, spawnerY);
                }
                npc.setSpeed(speed);
                npc.setDamage(damage);
                npc.setGuardPoint(Utils.getRandomIntegerInTheRange((int) (3 * tilesCellWidth), (int) (17 * tilesCellWidth), null),
                        Utils.getRandomIntegerInTheRange((int) (3 * tilesCellHeight), (int) (17 * tilesCellHeight), null), 6 * tilesCellWidth);
                npc.setOnGuard(true);
                new TimedTask(8000) {
                    @Override
                    public void performAction() {
                        npc.setGuardPoint(Utils.getRandomIntegerInTheRange((int) (3 * tilesCellWidth), (int) (17 * tilesCellWidth), null),
                                Utils.getRandomIntegerInTheRange((int) (3 * tilesCellHeight), (int) (17 * tilesCellHeight), null), 6 * tilesCellWidth);
                    }
                };
            }
        }
    }

    private void ended(){
        if(timerRenderer != null) timerRenderer.destroy();
        timerRenderer = new TextRenderer(timer.getTimeAsString(),
                endPanelXOffset + endPanelWidth * 2 / 3,
                endPanelYOffset + endPanelHeight / 4 - endPanelHeight / 8 / 2,
                endPanelWidth - 64 * GameView.density(),
                0,
                Paint.Align.CENTER,
                false, false);
        timerRenderer.show();

        if(distanceRenderer != null) distanceRenderer.destroy();
        distanceRenderer = new TextRenderer(distanceMeasurer.getDistanceAsString(),
                endPanelXOffset + endPanelWidth * 2 / 3,
                endPanelYOffset + endPanelHeight / 2 - endPanelHeight / 8 / 2,
                endPanelWidth - 64 * GameView.density(),
                0,
                Paint.Align.CENTER,
                false, false);
        distanceRenderer.show();

        float armorGained = Utils.calculateGainedArmor(timer.getTime(), distanceMeasurer.getDistance());
        String character = player.getClass().getSimpleName();
        PlayerStats.setUnitArmor(character, PlayerStats.getUnitArmor(character) + armorGained);
        PlayerStats.updateUnitStats(character);
        bonusMsRenderer = new TextRenderer(armorGained + "",
                endPanelXOffset + endPanelWidth * 2 / 3,
                endPanelYOffset + endPanelHeight * 3 / 4 - endPanelHeight / 8 / 2,
                endPanelWidth - 64 * GameView.density(),
                0,
                Paint.Align.CENTER,
                false, false);
        bonusMsRenderer.show();
    }

    private void endDelayed(){
        bonusMsText = new TextRenderer(GameView.string(R.string.armor),
                endPanelXOffset + endPanelWidth / 3,
                endPanelYOffset + endPanelHeight * 3 / 4,
                endPanelWidth - 64 * GameView.density(),
                endPanelHeight / 12,
                Paint.Align.CENTER,
                false, false);
        bonusMsText.show();

        timerImageRenderer = new ImageRenderer(endPanelXOffset + endPanelWidth / 3 - endPanelHeight / 8 / 2,
                endPanelYOffset + endPanelHeight / 4 - endPanelHeight / 8 / 2,
                endPanelHeight / 8, endPanelHeight / 8, TextureData.hourglass);

        distanceImageRenderer = new ImageRenderer(endPanelXOffset + endPanelWidth / 3 - endPanelHeight / 8 / 2,
                endPanelYOffset + endPanelHeight / 2 - endPanelHeight / 8 / 2,
                endPanelHeight / 8, endPanelHeight / 8, TextureData.footprints);
    }

    private void scalePanel(){
        byte counter = 0;
        if(widthEnd < endPanelWidth){
            int temp = widthEnd + (endPanelWidth - infoPanelWidth) / scaleFrames;
            widthEnd = Utils.min(temp, endPanelWidth);
        }
        if(heightEnd < endPanelHeight){
            int temp = heightEnd + (endPanelHeight - infoPanelHeight) / scaleFrames;
            heightEnd = Utils.min(temp, endPanelHeight);
        }
        if(xEndPosition > endPanelXOffset){
            int temp = xEndPosition - (infoPanelXOffset - endPanelXOffset) / scaleFrames;
            xEndPosition = Utils.max(temp, endPanelXOffset);
            counter++;
        }
        if(yEndPosition < endPanelYOffset){
            int temp = yEndPosition + (endPanelYOffset - infoPanelYOffset) / scaleFrames;
            yEndPosition = Utils.min(temp, endPanelYOffset);
            counter++;
        }

        if(counter == 0){
            if(!endDelayed){
                endDelayed = true;
                endDelayed();
            }
            timerRenderer.setMaxPaintSize(Utils.min(timerRenderer.getTextSize() + 1, endPanelHeight / 12));
            distanceRenderer.setMaxPaintSize(Utils.min(distanceRenderer.getTextSize() + 1, endPanelHeight / 12));
            bonusMsRenderer.setMaxPaintSize(Utils.min(bonusMsRenderer.getTextSize() + 1, endPanelHeight / 12));
            timerRenderer.update();
            distanceRenderer.update();
            bonusMsRenderer.update();
        }
    }

    @Override
    public void renderBackground() {
        int w;
        int h;
        w = GameView.getScreenWidth() / 2;
        h = GameView.getScreenHeight() / 2;
        if(iboId[0] != 0 && vboId[0] != 0) MyGL2dRenderer.drawLabel(cameraX + GameView.getScreenWidth() / 2, cameraY + player.getCollisionBox().height() / 2 + GameView.getScreenHeight() * 5 / 2/* + player.getSolidBox().height()*/, w, h, TextureData.atlas_training, 255,
                vboId[0], iboId[0], tilesWidth * tilesHeight * 6, 0);
    }

    @Override
    public void renderState(){
        renderBackground();
        TouchHandler.render(cameraX, cameraY);
        sortEntities(Entity.getEntities());
        if(getSelectedPc() != null){
            MyGL2dRenderer.drawLabel(getSelectedPc().getCollisionBox().left - 4 * GameView.density() + cameraX,
                    getSelectedPc().getCollisionBox().bottom - getSelectedPc().getCollisionBox().width() / 5 + cameraY,
                    getSelectedPc().getCollisionBox().width() + 8 * GameView.density(),
                    getSelectedPc().getCollisionBox().width() * 2 / 5,
                    TextureData.selected_character_visual, 255);
        }
        for(Entity x : Entity.getEntities()){
            if(cameraRect.intersects(x.getCollisionBox().left, x.getCollisionBox().top, x.getCollisionBox().right, x.getCollisionBox().bottom)){
                x.render(cameraX, cameraY);
            }
        }
        Button.renderButtons(getButtons(), getButtonsLock());
        if(!end){
            VisualEffect.render(cameraX, cameraY);
            renderSpells();
            drawInfoPanel();
            HealthBarRenderer.render(cameraX, cameraY);
        }else{
            VisualEffect.render(cameraX, cameraY);
            HealthBarRenderer.render(cameraX, cameraY);
            drawEndLayout();
        }
        fpsTextRenderer.setText("Average FPS : " + GameThread.avgFps);
        renderOver();
        drawLoading();
    }

    private void drawInfoPanel(){
        if(infoPanelImageRenderer != null) infoPanelImageRenderer.render(0, 0);
        if(timerImageRenderer != null) timerImageRenderer.render(0, 0);
        if(distanceImageRenderer != null) distanceImageRenderer.render(0, 0);
    }

    private void drawEndLayout(){
        MyGL2dRenderer.drawLabel(xEndPosition, yEndPosition, widthEnd, heightEnd, infoPanelImageRenderer.getImage(), 255);
        if(endDelayed){
            timerImageRenderer.render(0, 0);
            distanceImageRenderer.render(0, 0);
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
    @Override
    public boolean canMove(Rect rect, int xClearance, int yClearance) {
        try{
            ArrayList<int[]> tilesIntersected = getTilesIntersected(rect);
            for(int i = 0; i < tilesIntersected.size(); i++){
                if(tiles[tilesIntersected.get(i)[0]][tilesIntersected.get(i)[1]] == 1){
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

        int x1 = (int) (rect.left / tilesCellWidth);
        int y1 = (int) (rect.top / tilesCellHeight);
        int x2 = (int) (rect.right / tilesCellWidth);
        int y2 = (int) (rect.bottom / tilesCellHeight);



        for(int i = x1; i <= x2; i++){
            for(int j = y1; j <= y2; j++){
                list.add(new int[] {i, j});
            }
        }

        return list;
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
}