package com.tinslam.battleheart.states;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.buttons.rectanglebuttons.RectangleButton;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.UI.graphics.visualEffects.MoveCommandVisualEffect;
import com.tinslam.battleheart.UI.graphics.visualEffects.attachedToUnitVisualEffects.AttackCommandVisualEffect;
import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.gameUtility.SpellTouchInput;
import com.tinslam.battleheart.gameUtility.TouchHandler;
import com.tinslam.battleheart.spells.Spell;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.SpellConsts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * The class that holds fighting states.
 */
public abstract class BattleState extends State{
    private static BattleState battleState;
    public static byte inputCommand = Consts.INPUT_COMMAND_POINT;
    private final Object spellsLock = new Object();
    private ArrayList<Spell> spells = new ArrayList<>();
    private Rect mapRect = new Rect();
    private int mapTop = GameView.getScreenHeight() / 4;
    private boolean isOver = true;
    private boolean win = false;
    private int expGained = 0;
    private int goldGained = 0;
    private final Object spellButtonsLock = new Object();
    private ArrayList<RectangleButton> spellButtons = new ArrayList<>();
    private Texture spellCasterPortrait;
    TextRenderer goldTextRenderer;
    private Pc selectedPc = null;
    float cameraX = 0, cameraY = 0;

    private final Object portraitsLock = new Object();
    private ArrayList<RectangleButton> portraits = new ArrayList<>();
    private HashMap<Pc, RectangleButton> portraitsMap = new HashMap<>();
    private int portraitWidth = (int) (48 * GameView.density());
    private int portraitHeight = (int) (48 * GameView.density());
    private int healthBarHeight = (int) (8 * GameView.density());
    private int portraitGap = (int) (8 * GameView.density());

    private static final Object inputsLock = new Object();
    private ArrayList<Object[]> inputsQueue = new ArrayList<>();

    /**
     *
     * Constructor.
     */
    BattleState(){
        battleState = this;
    }

    @Override
    public void handleBackPressed(){
        if(win) return;
        isOver = true;
        onBackPressed();
    }

    public abstract void onBackPressed();

    public abstract void pcDied(Pc pc);

    void initPcPortraits(){
        int i = 0;
        for(RectangleButton button : portraits){
            button.destroy();
        }
        portraitsMap.clear();
        portraits.clear();
        for(final Pc x : Pc.getPcs()){
            Texture image = new Texture(PlayerStats.getUnitPortrait(x.getClass().getSimpleName()).getTexture(), portraitWidth, portraitHeight);
            RectangleButton button = new RectangleButton(portraitGap,
                    GameView.getScreenHeight() - portraitGap - healthBarHeight - i * (portraitHeight + portraitGap + healthBarHeight) - portraitHeight,
                    image, image, true) {
                @Override
                public boolean onDown() {
                    return true;
                }

                @Override
                public boolean onUp() {
                    if(x.doesExist()){
                        setSelectedPc(x);
                    }
                    return true;
                }
            };
            portraits.add(button);
            portraitsMap.put(x, button);
            i++;
        }
    }

    void disablePcPortrait(Pc pc){
        RectangleButton portrait = portraitsMap.get(pc);
        if(portrait != null) portrait.setActive(false);
    }

    /**
     * Initializes the state. Is called when switching to a new state.
     */
    @Override
    public void startState() {
        Spell.clear();
        initGoldRenderers();
        initBackground();
        startBattleState();
        isOver = false;
    }

    protected void loadSpells(){
        for(Pc x : Pc.getPcs()){
            x.loadSpells(getSpells());
        }
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
                    new Event() {
                        @Override
                        public void performAction() {
                            if(BattleState.getBattleState() instanceof DungeonState){
                                if(!x.isTargeting()) DungeonState.getDungeonState().replaySpellClicked(x);
                            }
                            if(!x.isReady()) return;
                            if(x.isTargeting()){
                                setSpellCasterPortrait(null);
                                x.onClick();
                            }else{
                                x.cast();
                            }
                        }
                    };
                    return true;
                }
            };
            rectangleButton.getTextRenderer().show();
            x.setCancelArea(new Rect(rectangleButton.getX(), rectangleButton.getY(), rectangleButton.getX() + rectangleButton.getWidth(), rectangleButton.getY() + rectangleButton.getHeight()));
            getSpellButtons().add(rectangleButton);
            i++;
        }
    }

    public abstract void initBackground();

    /**
     * Initializes the state.
     */
    public abstract void startBattleState();

    private void initGoldRenderers(){
        goldTextRenderer = new TextRenderer("" + goldGained,
                GameView.getScreenWidth() * 9 / 10 + 2 * GameView.density(),
                GameView.getScreenHeight() / 20 - 2 * GameView.density() + (GameView.getScreenWidth() / 20 + 4 * GameView.density()) / 2,
                GameView.getScreenWidth() / 10 - 4 * GameView.density(),
                GameView.getScreenHeight() / 20,
                Paint.Align.LEFT,
                false,
                false);
    }

    /**
     * Is called when player defeats all waves.
     */
    public void win(){
        // If this object is not the current arena state obsolete it.
        if(this != battleState) return;
        if(win) return;

        isOver = true;
        setSlowMo(false);
        win = true;
        setSelectedPc(null);

        Spell.clear();
        for(Button x : spellButtons){
            x.setActive(false);
        }
        TouchHandler.clear();
        SpellTouchInput.clear();
        battleStateWin();
    }

    public abstract void battleStateWin();

    /**
     * Is called when player loses all units.
     */
    public void lose(){
        // If this object is not the current arena state obsolete it.
        if(this != battleState) return;

        isOver = true;
        battleStateLose();
    }

    public abstract void battleStateLose();

    public void removeSpellsOfTheUnit(Pc pc){
        int gap = (int) (8 * GameView.density());
        int width = SpellConsts.PORTRAIT_WIDTH;
        if(getTouchInput() != null){
            if(getTouchInput().getEntity() == pc){
                touchInput = null;
                setTouchInput(null);
            }
        }
        int i = 0;
        for(final Spell x : spells){
            for(final Button y : spellButtons){
                if(y.getImage() == x.getPortrait()){
                    x.setCancelArea(Utils.translateRect(x.getCancelArea(), -i * (width + gap), 0));
                    y.translate(-i * (width + gap), 0);
                    if(pc == x.getCaster()){
                        y.getTextRenderer().destroy();
                        new Event() {
                            @Override
                            public void performAction() {
                                spellButtons.remove(y);
                                spells.remove(x);
                            }
                        };
                        y.setActive(false);
                        i++;
                    }
                }
            }
        }
        if(touchInput != null){
            if(touchInput.getEntity() == pc){
                touchInput.destroy();
            }
        }
    }

    /**
     * Ticks the state.
     */
    @Override
    public void tick() {
        processInputs();
        tickBattleState();
    }

    public abstract void tickBattleState();

    /**
     * Renders the state.
     * Sorts the entities list to have them be drawn in order depending on how close they are.
     * Draws the background.
     */
    @Override
    public void render() {
        sortEntities(Entity.getEntities());
        renderBackground();
        renderBattleState();
        if(selectedPc != null){
            MyGL2dRenderer.drawLabel(getSelectedPc().getCollisionBox().left - 4 * GameView.density() + cameraX,
                    getSelectedPc().getCollisionBox().bottom - getSelectedPc().getCollisionBox().width() / 5 + cameraY,
                    getSelectedPc().getCollisionBox().width() + 8 * GameView.density(),
                    getSelectedPc().getCollisionBox().width() * 2 / 5,
                    TextureData.selected_character_visual, 255);
        }
        if(!win){
            renderSpells();
            renderGold();
        }
    }

    public abstract void renderBackground();

    public abstract void renderBattleState();

    private void renderPortraits(){
        for(RectangleButton x : portraits){
            x.render();
            if(!x.isActive()){
                MyGL2dRenderer.drawLabel(x.getX(), x.getY(), x.getWidth(), x.getHeight(), TextureData.solid_black,160);
            }
        }
        for(Pc pc : Pc.getPcs()){
            RectangleButton button = portraitsMap.get(pc);
            if(button != null) pc.getHealthBar().render(new Rect(button.getX(), button.getY() + button.getHeight(), button.getX() + button.getWidth(), button.getY() + button.getHeight() + healthBarHeight));
        }
    }

    private void renderGold(){
        MyGL2dRenderer.drawLabel(GameView.getScreenWidth() * 8 / 10 - 2 * GameView.density(),
                GameView.getScreenHeight() / 20 - 2 * GameView.density(),
                GameView.getScreenWidth() / 20 + 6 * GameView.density() + GameView.getScreenWidth() / 5,
                GameView.getScreenWidth() / 20 + 4 * GameView.density(),
                TextureData.yellow_panel,
                255);
        MyGL2dRenderer.drawLabel(GameView.getScreenWidth() * 8 / 10,
                GameView.getScreenHeight() / 20,
                GameView.getScreenWidth() / 20,
                GameView.getScreenWidth() / 20,
                TextureData.gold,
                255);
    }

    void renderSpells(){
        for(RectangleButton x : spellButtons){
            x.render();
        }
        int i = 0;
        int xOffset = (int) (16 * GameView.density());
        int yOffset = (int) (12 * GameView.density());
        int gap = (int) (8 * GameView.density());
        int width = SpellConsts.PORTRAIT_WIDTH;
        int height = SpellConsts.PORTRAIT_HEIGHT;
        for(Spell x : spells){
            if(!x.isReady()){
                MyGL2dRenderer.drawLabel(xOffset + i * (width + gap),
                        yOffset,
                        width,
                        height,
                        TextureData.solid_black,
                        127);
                MyGL2dRenderer.drawLabel(xOffset + i * (width + gap),
                        yOffset + (100 - x.getCdLeftPercentage()) * height / 100,
                        width,
                        yOffset + height - (yOffset + (100 - x.getCdLeftPercentage()) * height / 100),
                        TextureData.solid_black,
                        190);
            }
            i++;
        }
    }

    @Override
    public void renderOver(){
        renderOverBattleState();
        renderPortraits();
        if(getSlowMo()){
            MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.solid_black, 100);
            if(spellCasterPortrait != null){
                int xOffset = GameView.getScreenWidth() / 2 - spellCasterPortrait.getWidth() / 2;
                int yOffset = (int) (16 * GameView.density());
                MyGL2dRenderer.drawLabel(xOffset, yOffset, spellCasterPortrait.getWidth(), spellCasterPortrait.getHeight(), spellCasterPortrait.getTexture(), 255);
            }
        }
    }

    public abstract void renderOverBattleState();

    /**
     * Sorts the entities list to have them be drawn in order depending on how close they are.
     */
    void sortEntities(ArrayList<Entity> entities){
        Collections.sort(entities, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                if(e1.getCollisionBox().bottom < e2.getCollisionBox().bottom) return -1;
                else if(e1.getCollisionBox().bottom == e2.getCollisionBox().bottom) return 0;
                else return 1;
            }
        });
    }

    /**
     * Checks whether an area on the map is possible to move to.
     */
    public abstract boolean canMove(Rect rect, int xClearance, int yClearance);

    public boolean canMoveToDestination(Unit unit){
        return canMove(new Rect((int) unit.getX2(), (int) unit.getY2() + unit.getCollisionBox().height() - unit.getSolidBox().height(), (int) unit.getX2() + unit.getSolidBox().width(), (int) (unit.getY2() + unit.getCollisionBox().height())), unit.getXClearance(), unit.getYClearance());
    }

    /**
     * Checks whether an area on the map is out of bounds.
     */
    public abstract boolean isOutOfBounds(int x, int y);

    /**
     * Is called when state is to be changed.
     */
    @Override
    public void endState() {
        spells.clear();
        spellButtons.clear();
    }

    public void queueEvent(Object[] objects){
        inputsQueue.add(objects);
    }

    private void processInputs(){
        for(Object[] inputs : inputsQueue){
            if(((String) inputs[0]).equalsIgnoreCase("move")){
                Pc pc = (Pc) inputs[1];
                pc.setTarget(null);
                pc.commandMove((float) inputs[2], (float) inputs[3]);
            }else if(((String) inputs[0]).equalsIgnoreCase("attack")){
                Pc pc = (Pc) inputs[1];
                Unit target = (Unit) inputs[2];
                pc.setTarget(target);
                pc.performAction();
            }
        }
        inputsQueue.clear();
    }

    public boolean onActionDown(MotionEvent event){
        if(isWin()) return true;
        int mx = (int) (event.getX() - cameraX);
        int my = (int) (event.getY() - cameraY);
        for(Pc x : Pc.getPcs()){
            if(x.getCollisionBox().contains(mx, my)) {
                new TouchHandler(x, event.getPointerId(0));
                break;
            }
        }
        return true;
    }

    public boolean onActionPointerDown(MotionEvent event){
        if(isWin()) return true;
        int mx = (int) (event.getX(event.getActionIndex()) - cameraX);
        int my = (int) (event.getY(event.getActionIndex()) - cameraY);
        for(Pc x : Pc.getPcs()){
            if(x.getCollisionBox().contains(mx, my)){
                new TouchHandler(x, event.getPointerId(event.getActionIndex()));
                break;
            }
        }
        return true;
    }

    public boolean onActionMove(MotionEvent event){
        if(isWin()) return true;
        for(int i = 0; i < event.getPointerCount(); i++){
            TouchHandler cth = TouchHandler.getTouchHandler(event.getPointerId(i));
            if(cth != null){
                cth.setDst(null);
                int mx = (int) (event.getX(i) - cameraX);
                int my = (int) (event.getY(i) - cameraY);
                if(my < GameView.getScreenHeight() / 2) cth.setY2(Utils.max(getMapRect().top + cth.getSrc().getCollisionBox().height() - cth.getSrc().getCollisionBox().width() / 5, my));
                else cth.setY2(Utils.min(getMapRect().bottom - cth.getSrc().getCollisionBox().width() / 5, my));
                if(mx < GameView.getScreenWidth() / 2) cth.setX2(Utils.max(getMapRect().left + cth.getSrc().getCollisionBox().width() / 2, mx));
                else cth.setX2(Utils.min(getMapRect().right - cth.getSrc().getCollisionBox().width() / 2, mx));
                ArrayList<Unit> units = new ArrayList<>();
                for(Unit x : Unit.getUnits()){
                    if(x.getCollisionBox().contains(mx, my)){
                        units.add(x);
                    }
                }
                cth.setDst(cth.getSrc().choosePriorityUnit(units));
            }
        }
        return true;
    }

    /**
     * Handles touch input when action is releasing and is the first finger touching the screen.
     * Performs the action of the touch handler if it exists.
     */
    @Override
    public boolean onActionUp(MotionEvent event) {
        if(isWin()) return true;

        int mx = (int) (event.getX() - cameraX);
        int my = (int) (event.getY() - cameraY);

        TouchHandler cth = TouchHandler.getTouchHandler(event.getPointerId(0));
        if(cth != null){
            if(cth.isValid()){
                cth.perform();
                return true;
            }else{
                TouchHandler.removeTouchHandler(cth);
            }
        }

        if(this instanceof DungeonState) if(((DungeonState) this).onUp(mx, my)) return true;

        if(selectedPc != null){
            float x2, y2;

            if(my < GameView.getScreenHeight() / 2) y2 = Utils.max(getMapRect().top + selectedPc.getCollisionBox().height() - selectedPc.getCollisionBox().width() / 5, my);
            else y2 = Utils.min(getMapRect().bottom - selectedPc.getCollisionBox().width() / 5, my);
            if(mx < GameView.getScreenWidth() / 2) x2 = Utils.max(getMapRect().left + selectedPc.getCollisionBox().width() / 2, mx);
            else x2 = Utils.min(getMapRect().right - selectedPc.getCollisionBox().width() / 2, mx);
            ArrayList<Unit> units = new ArrayList<>();
            for(Unit x : Unit.getUnits()){
                if(x.getCollisionBox().contains(mx, my)){
                    units.add(x);
                }
            }
            final Unit dst = selectedPc.choosePriorityUnit(units);

            if(dst == null){
                if(BattleState.getBattleState() instanceof DungeonState){
                    DungeonState.getDungeonState().replayCommandMove(selectedPc, x2 - selectedPc.getCollisionBox().width() / 2, y2 - selectedPc.getCollisionBox().height());
                }
                BattleState.getBattleState().queueEvent(new Object[] {"move", selectedPc, x2 - selectedPc.getCollisionBox().width() / 2, y2 - selectedPc.getCollisionBox().height()});
//                selectedPc.setTarget(null);
//                selectedPc.commandMove(x2 - selectedPc.getCollisionBox().width() / 2, y2 - selectedPc.getCollisionBox().height());
                new MoveCommandVisualEffect(x2, y2);
            }else{
                if(BattleState.getBattleState() instanceof DungeonState){
                    DungeonState.getDungeonState().replayCommandAttack(selectedPc, dst);
                }
                BattleState.getBattleState().queueEvent(new Object[] {"attack", selectedPc, dst});
//                selectedPc.setTarget(dst);
//                selectedPc.performAction();
                new AttackCommandVisualEffect(dst);
            }
        }
        return true;
    }

    /**
     * Handles touch input when action is releasing and is not the first finger touching the screen.
     * Performs the action of the touch handler if it exists.
     */
    @Override
    public boolean onActionPointerUp(MotionEvent event) {
        if(isWin()) return true;

        int mx = (int) (event.getX(event.getActionIndex()) - cameraX);
        int my = (int) (event.getY(event.getActionIndex()) - cameraY);

        TouchHandler cth = TouchHandler.getTouchHandler(event.getPointerId(event.getActionIndex()));
        if(cth != null){
            if(cth.isValid()){
                cth.perform();
                return true;
            }else{
                TouchHandler.removeTouchHandler(cth);
            }
        }

        if(this instanceof DungeonState) if(((DungeonState) this).onUp(mx, my)) return true;

        if(selectedPc != null){
            float x2, y2;

            if(my < GameView.getScreenHeight() / 2) y2 = Utils.max(getMapRect().top + selectedPc.getCollisionBox().height() - selectedPc.getCollisionBox().width() / 5, my);
            else y2 = Utils.min(getMapRect().bottom - selectedPc.getCollisionBox().width() / 5, my);
            if(mx < GameView.getScreenWidth() / 2) x2 = Utils.max(getMapRect().left + selectedPc.getCollisionBox().width() / 2, mx);
            else x2 = Utils.min(getMapRect().right - selectedPc.getCollisionBox().width() / 2, mx);
            ArrayList<Unit> units = new ArrayList<>();
            for(Unit x : Unit.getUnits()){
                if(x.getCollisionBox().contains(mx, my)){
                    units.add(x);
                }
            }
            final Unit dst = selectedPc.choosePriorityUnit(units);

            if(dst == null){
                if(BattleState.getBattleState() instanceof DungeonState){
                    DungeonState.getDungeonState().replayCommandMove(selectedPc, x2 - selectedPc.getCollisionBox().width() / 2, y2 - selectedPc.getCollisionBox().height());
                }
                BattleState.getBattleState().queueEvent(new Object[] {"move", selectedPc, x2 - selectedPc.getCollisionBox().width() / 2, y2 - selectedPc.getCollisionBox().height()});
//                selectedPc.setTarget(null);
//                selectedPc.commandMove(x2 - selectedPc.getCollisionBox().width() / 2, y2 - selectedPc.getCollisionBox().height());
                new MoveCommandVisualEffect(x2, y2);
            }else{
                if(BattleState.getBattleState() instanceof DungeonState){
                    DungeonState.getDungeonState().replayCommandAttack(selectedPc, dst);
                }
                BattleState.getBattleState().queueEvent(new Object[] {"attack", selectedPc, dst});
//                selectedPc.setTarget(dst);
//                selectedPc.performAction();
                new AttackCommandVisualEffect(dst);
            }
        }
        return true;
    }

    /**
     *
     * @return The most recently created instance of the ArenaState class.
     */
    public static BattleState getBattleState(){
        return battleState;
    }

    /**
     * @return Whether the battle has ended or not started yet.
     */
    public boolean isOver(){
        return isOver;
    }

    public void addExpGained(int exp){
        expGained += exp;
    }

    public void addGoldGained(int gold){
        goldGained += gold;
        goldTextRenderer.setText("" + goldGained);
    }

    int getMapTop() {
        return mapTop;
    }

    public void setSpellCasterPortrait(Texture spellCasterPortrait) {
        this.spellCasterPortrait = spellCasterPortrait;
    }

    public Object getSpellsLock() {
        return spellsLock;
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public Object getSpellButtonsLock() {
        return spellButtonsLock;
    }

    public ArrayList<RectangleButton> getSpellButtons() {
        return spellButtons;
    }

    void setMapTop(int mapTop) {
        this.mapTop = mapTop;
    }

    public void setSpells(ArrayList<Spell> spells) {
        this.spells = spells;
    }

    public void setSpellButtons(ArrayList<RectangleButton> spellButtons) {
        this.spellButtons = spellButtons;
    }

    public Texture getSpellCasterPortrait() {
        return spellCasterPortrait;
    }

    int getExpGained() {
        return expGained;
    }

    public Rect getMapRect() {
        return mapRect;
    }

    void setExpGained(int expGained) {
        this.expGained = expGained;
    }

    int getGoldGained() {
        return goldGained;
    }

    public void setGoldGained(int goldGained) {
        this.goldGained = goldGained;
    }

    boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public float getCameraX(){
        return cameraX;
    }

    public float getCameraY(){
        return cameraY;
    }

    public Pc getSelectedPc() {
        return selectedPc;
    }

    public boolean isInCamera(Entity entity){
        return new Rect((int) -cameraX, (int) -cameraY, (int) -cameraX + GameView.getScreenWidth(), (int) -cameraY + GameView.getScreenHeight()).intersects(entity.getCollisionBox().left, entity.getCollisionBox().top, entity.getCollisionBox().right, entity.getCollisionBox().bottom);
    }

    public void setSelectedPc(Pc selectedPc) {
        this.selectedPc = selectedPc;
        if(selectedPc == null) return;
        if(BattleState.getBattleState() instanceof DungeonState){
            DungeonState.getDungeonState().replaySelectedPc(selectedPc);
            if(DungeonState.getDungeonState().isFollowingDisabled()) return;
            float radius = 8 * ((DungeonState) BattleState.getBattleState()).tileCellWidth;
            for(Pc x : Pc.getPcs()){
                if(x == getSelectedPc()){
                    x.setFollow(false, null, 0);
                }else{
                    x.setFollow(true, getSelectedPc(), radius);
                }
            }
        }
    }
}
