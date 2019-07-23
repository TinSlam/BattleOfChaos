package com.tinslam.battleheart.entities.units;

import android.graphics.Rect;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Animations.CastAnimation;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.HealthBarRenderer;
import com.tinslam.battleheart.UI.graphics.visualEffects.DamageVisualEffect;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.entities.units.PCs.Knight;
import com.tinslam.battleheart.entities.units.PCs.Priest;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.Node;
import com.tinslam.battleheart.gameUtility.PathFinding;
import com.tinslam.battleheart.gameUtility.TouchHandler;
import com.tinslam.battleheart.quests.KillQuest;
import com.tinslam.battleheart.spells.Spell;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.states.ReplayState;
import com.tinslam.battleheart.states.State;
import com.tinslam.battleheart.utils.TimedTask;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.shapes.Line2d;

import java.util.ArrayList;

/**
 * The class that includes all the units in the game.
 */
public abstract class Unit extends Entity {
    private final static Object unitsLock = new Object();
    private static ArrayList<Unit> units = new ArrayList<>();

    public final Object commandsLock = new Object();
    public ArrayList<Byte> commands = new ArrayList<>();
    private final Object spellsLock = new Object();
    private ArrayList<Spell> spells = new ArrayList<>();
    private float x2, y2;
    private byte state = Consts.STATE_HOLD;
    private byte team;
    private float hp = 100, maxHp = hp;
    private boolean vulnerability = true;
    private float speed = 3;
    private float projectileSpeed = 20;
    private Animation attackRightAnimation;
    private Animation attackLeftAnimation;
    private Animation moveRightAnimation;
    private Animation moveLeftAnimation;
    private Animation idleLeftAnimation;
    private Animation idleRightAnimation;
    private int attackCD = 1000;
    private float damage = 10;
    private float armor = 2;
    private HealthBarRenderer healthBar;
    private boolean showHealthBar = true;
    private Unit target = null;
    private boolean canAttack = true;
    private float attackRange;
    private int aggroRange = (int) (300 * GameView.density());
    private float guardX, guardY, guardRadius;
    private Unit followingUnit;
    private float followRadius;
    private boolean onGuard = false;
    protected boolean following = false;
    private final Object attackersLock = new Object();
    private ArrayList<Unit> attackers = new ArrayList<>();
    private ArrayList<Unit> followers = new ArrayList<>();
    private Rect solidBox = new Rect();
    public Object[] pathObject;
    private boolean moveCd = false;

    /**
     * Constructor.
     * @param x The x position.
     * @param y The y position.
     * @param team The side/team/faction the unit belongs to.
     */
    public Unit(float x, float y, byte team) {
        super(x, y);

        setX2(x);
        setY2(y);
        setTeam(team);
        if(team != Consts.TEAM_NEUTRAL) healthBar = new HealthBarRenderer(this);

        addUnit(this);

        loadAnimations();

        loadStats();

        setHp(getMaxHp());
    }

    /**
     * Loads the stats of the unit. (Hp, Damage, ...)
     */
    public abstract void loadStats();

    /**
     * This method is triggered every time the unit gets attacked and its hp doesn't drop to 0 or less.
     * @param attacker The attacker unit.
     */
    public abstract void reactToBeingAttacked(Unit attacker);


    /**
     * This method is triggered every time an ally unit signals getting attacked and its hp doesn't drop to 0 or less.
     * @param ally The ally being attacked.
     * @param attacker The attacker unit.
     */
    public abstract void reactToAllyBeingAttacked(Unit ally, Unit attacker);

    /**
     * This method checks whether the unit passed as a parameter is in the attack range of the unit.
     * @param unit The unit to check for.
     * @return True if the unit if in attack range. False if not.
     */
    public abstract boolean isInAttackRange(Unit unit);

    /**
     * Destroys the unit.
     */
    public abstract void destroyUnit();

    /**
     * Ticks the unit.
     */
    public abstract void tickUnit();

    /**
     * Renders the unit.
     */
    public abstract void renderUnit(float xOffset, float yOffset);

    /**
     * Initializes the animations to be ready to used.
     */
    public abstract void loadAnimations();

    /**
     * Attacks or follows the target depending on begin in range or not. Is called every frame automatically if the unit has an attacking state.
     */
    private void attack(){
        if(getAnimation() == attackLeftAnimation || getAnimation() == attackRightAnimation) return;
        if(target != null && target.doesExist()){
            if(isInAttackRange(target)){
                if(canAttack){
                    setState(Consts.STATE_ATTACK);
                    setCanAttack(false);
                    if(target.getCollisionBox().centerX() < getCollisionBox().centerX()) setAnimation(attackLeftAnimation); else setAnimation(attackRightAnimation);
                }
            }else{
                commandAttack(getTarget());
            }
        }else{
            resetCommands();
        }
    }

    public void setGuardPoint(float x, float y, float radius){
        guardX = x;
        guardY = y;
        guardRadius = radius;
    }

    public void setOnGuard(boolean flag){
        onGuard = flag;
//        if(flag){
//            setX2(guardX);
//            setY2(guardY);
//            setState(Consts.STATE_MOVE);
//            move();
//        }else{
//            setState(Consts.STATE_HOLD);
//        }
    }

    /**
     * Has the unit move in range of its target. Is called automatically on every frame if the unit is on attacking state. Must be overrode for different approaches.
     * Current approach is getting in melee range. (Side by side)
     */
    public abstract boolean initTargetLocation();

    /**
     * Moves the unit towards the (x2, y2) position. Is called automatically on every frame if the unit is on moving state.
     */
    protected void move(){
        if(BattleState.getBattleState() instanceof DungeonState){
            if(pathObject == null){
                return;
            }
            final DungeonState dungeonState = (DungeonState) BattleState.getBattleState();
            final float tileWidth = dungeonState.tileCellWidth;
            final float tileHeight = dungeonState.tileCellHeight;
            final float endSolidBoxX = x2;
            final float endSolidBoxY = y2 + getCollisionBox().height() - solidBox.height();
            //noinspection unchecked
            ArrayList<int[]> path = (ArrayList<int[]>) pathObject[0];
            boolean success = (boolean) pathObject[1];

            // Hard set position if gets close enough to the point.
            if(Utils.distance(endSolidBoxX, endSolidBoxY, solidBox.left, solidBox.top) < 4 * GameView.density() + getSpeed() * GameView.density()){
                boolean b1 = setX(x2);
                boolean b2 = setY(y2);
                if(!b1){
                    x2 = getX();
                }
                if(!b2){
                    y2 = getY();
                }
            }
            // Don't move if already at target point.
            if(getX() == x2 && getY() == y2){
                if(state == Consts.STATE_MOVE) setState(Consts.STATE_HOLD);
            }else{
                if(!pathIntersected(solidBox, endSolidBoxX, endSolidBoxY)){
//                    System.out.println("Here.");
                    // Hard set position if gets close enough to the point.
                    if(Utils.distance(endSolidBoxX, endSolidBoxY, solidBox.left, solidBox.top) < getSpeed() * GameView.density()){
                        boolean b1 = setX(x2);
                        boolean b2 = setY(y2);
                        if(!b1){
                            x2 = getX();
                        }
                        if(!b2){
                            y2 = getY();
                        }
                    }
                    // Don't move if already at target point.

                    if(getX() == x2 && getY() == y2){
                        if(state == Consts.STATE_MOVE) setState(Consts.STATE_HOLD);
                    }else{
                        // Move to target point.
                        float angle = (float) Math.atan2(solidBox.top - endSolidBoxY, solidBox.left - endSolidBoxX);
                        float c = -(float) Math.cos(angle);
                        float s = -(float) Math.sin(angle);
                        if(getAnimation() == moveRightAnimation || getAnimation() == moveLeftAnimation){
                            if(c > 0.173){
                                setAnimation(moveRightAnimation);
                            }else if(c < -0.173){
                                setAnimation(moveLeftAnimation);
                            }
                            getAnimation().resume();
                        }else{
                            if(c > 0) setAnimation(moveRightAnimation); else setAnimation(moveLeftAnimation);
                        }
                        boolean b1 = addX(c * getSpeed() * GameView.density());
                        boolean b2 = addY(s * getSpeed() * GameView.density());
                        if(!b1){
//                            x2 = getX();
                        }
                        if(!b2){
//                            y2 = getY();
                        }
                    }
                }else{
                    int point = 0;
                    for(int i = path.size() - 1; i > 0; i--){
                        // Maybe for last node the arguments of the pathIntersected method must be changed.
                        if(!pathIntersected(solidBox, path.get(i)[0], path.get(i)[1])){
                            point = i;
                            break;
                        }
                    }
                    Rect rect = new Rect((int) (path.get(path.size() - 1)[0] * tileWidth), (int) (path.get(path.size() - 1)[1] * tileHeight), (int) (path.get(path.size() - 1)[0] * tileWidth + tileWidth), (int) (path.get(path.size() - 1)[1] * tileHeight + tileHeight));
                    if(success && Utils.isInRect(solidBox.left, solidBox.top, rect) && !Utils.isInRect(x2, y2, rect)){
                        return;
                    }
                    float angle = (float) Math.atan2(solidBox.top - (path.get(point)[1] * tileHeight), solidBox.left - path.get(point)[0] * tileWidth);
                    float c = -(float) Math.cos(angle);
                    float s = -(float) Math.sin(angle);
                    if(getAnimation() == moveRightAnimation || getAnimation() == moveLeftAnimation){
                        if(c > 0.173){
                            setAnimation(moveRightAnimation);
                        }else if(c < -0.173){
                            setAnimation(moveLeftAnimation);
                        }
                        getAnimation().resume();
                    }else{
                        if(c > 0) setAnimation(moveRightAnimation); else setAnimation(moveLeftAnimation);
                    }
//                    System.out.println("Stuck here with point " + point + " " + getX() + " " + getY() + " " + x2 + " " + y2);
                    addX(c * getSpeed() * GameView.density());
                    addY(s * getSpeed() * GameView.density());
                }
            }
            if(!success && !path.isEmpty()){
                pathObject[1] = true;
                setX2(path.get(path.size() - 1)[0] * tileWidth);
                setY2(path.get(path.size() - 1)[1] * tileHeight - getCollisionBox().height() + solidBox.height());
            }
        }else if(BattleState.getBattleState() instanceof ReplayState){
            if(pathObject == null){
                return;
            }
            final ReplayState dungeonState = (ReplayState) BattleState.getBattleState();
            final float tileWidth = dungeonState.tileCellWidth;
            final float tileHeight = dungeonState.tileCellHeight;
            final float endSolidBoxX = x2;
            final float endSolidBoxY = y2 + getCollisionBox().height() - solidBox.height();
            //noinspection unchecked
            ArrayList<int[]> path = (ArrayList<int[]>) pathObject[0];
            boolean success = (boolean) pathObject[1];

            // Hard set position if gets close enough to the point.
            if(Utils.distance(endSolidBoxX, endSolidBoxY, solidBox.left, solidBox.top) < 4 * GameView.density() + getSpeed() * GameView.density()){
                boolean b1 = setX(x2);
                boolean b2 = setY(y2);
                if(!b1){
                    x2 = getX();
                }
                if(!b2){
                    y2 = getY();
                }
            }
            // Don't move if already at target point.
            if(getX() == x2 && getY() == y2){
                if(state == Consts.STATE_MOVE) setState(Consts.STATE_HOLD);
            }else{
                if(!pathIntersected(solidBox, endSolidBoxX, endSolidBoxY)){
//                    System.out.println("Here.");
                    // Hard set position if gets close enough to the point.
                    if(Utils.distance(endSolidBoxX, endSolidBoxY, solidBox.left, solidBox.top) < getSpeed() * GameView.density()){
                        boolean b1 = setX(x2);
                        boolean b2 = setY(y2);
                        if(!b1){
                            x2 = getX();
                        }
                        if(!b2){
                            y2 = getY();
                        }
                    }
                    // Don't move if already at target point.

                    if(getX() == x2 && getY() == y2){
                        if(state == Consts.STATE_MOVE) setState(Consts.STATE_HOLD);
                    }else{
                        // Move to target point.
                        float angle = (float) Math.atan2(solidBox.top - endSolidBoxY, solidBox.left - endSolidBoxX);
                        float c = -(float) Math.cos(angle);
                        float s = -(float) Math.sin(angle);
                        if(getAnimation() == moveRightAnimation || getAnimation() == moveLeftAnimation){
                            if(c > 0.173){
                                setAnimation(moveRightAnimation);
                            }else if(c < -0.173){
                                setAnimation(moveLeftAnimation);
                            }
                            getAnimation().resume();
                        }else{
                            if(c > 0) setAnimation(moveRightAnimation); else setAnimation(moveLeftAnimation);
                        }
                        boolean b1 = addX(c * getSpeed() * GameView.density());
                        boolean b2 = addY(s * getSpeed() * GameView.density());
                        if(!b1){
//                            x2 = getX();
                        }
                        if(!b2){
//                            y2 = getY();
                        }
                    }
                }else{
                    int point = 0;
                    for(int i = path.size() - 1; i > 0; i--){
                        // Maybe for last node the arguments of the pathIntersected method must be changed.
                        if(!pathIntersected(solidBox, path.get(i)[0], path.get(i)[1])){
                            point = i;
                            break;
                        }
                    }
                    Rect rect = new Rect((int) (path.get(path.size() - 1)[0] * tileWidth), (int) (path.get(path.size() - 1)[1] * tileHeight), (int) (path.get(path.size() - 1)[0] * tileWidth + tileWidth), (int) (path.get(path.size() - 1)[1] * tileHeight + tileHeight));
                    if(success && Utils.isInRect(solidBox.left, solidBox.top, rect) && !Utils.isInRect(x2, y2, rect)){
                        return;
                    }
                    float angle = (float) Math.atan2(solidBox.top - (path.get(point)[1] * tileHeight), solidBox.left - path.get(point)[0] * tileWidth);
                    float c = -(float) Math.cos(angle);
                    float s = -(float) Math.sin(angle);
                    if(getAnimation() == moveRightAnimation || getAnimation() == moveLeftAnimation){
                        if(c > 0.173){
                            setAnimation(moveRightAnimation);
                        }else if(c < -0.173){
                            setAnimation(moveLeftAnimation);
                        }
                        getAnimation().resume();
                    }else{
                        if(c > 0) setAnimation(moveRightAnimation); else setAnimation(moveLeftAnimation);
                    }
//                    System.out.println("Stuck here with point " + point + " " + getX() + " " + getY() + " " + x2 + " " + y2);
                    addX(c * getSpeed() * GameView.density());
                    addY(s * getSpeed() * GameView.density());
                }
            }
            if(!success && !path.isEmpty()){
                pathObject[1] = true;
                setX2(path.get(path.size() - 1)[0] * tileWidth);
                setY2(path.get(path.size() - 1)[1] * tileHeight - getCollisionBox().height() + solidBox.height());
            }
        }else{
            // Hard set position if gets close enough to the point.
            if(Utils.distance(x2, y2, getX(), getY()) < getSpeed() * GameView.density()){
                setX(x2);
                setY(y2);
            }
            // Don't move if already at target point.
            if(getX() == x2 && getY() == y2){
                if(state == Consts.STATE_MOVE) setState(Consts.STATE_HOLD);
            }else{
                // Move to target point.
                float angle = (float) Math.atan2(getY() - y2, getX() - x2);
                float c = -(float) Math.cos(angle);
                float s = -(float) Math.sin(angle);
                if(c > 0) setAnimation(moveRightAnimation); else setAnimation(moveLeftAnimation);
                addX(c * getSpeed() * GameView.density());
                addY(s * getSpeed() * GameView.density());
            }
        }
        if(getX() == x2 && getY() == y2){
            pathObject = null;
            commands.remove(0);
            setState(Consts.STATE_HOLD);
        }
    }

    private boolean pathIntersected(Rect rect, float x, float y){
        if(BattleState.getBattleState() instanceof DungeonState){
            DungeonState dungeonState = (DungeonState) BattleState.getBattleState();
            int tileWidth = dungeonState.tileCellWidth;
            int tileHeight = dungeonState.tileCellHeight;

            Rect src = new Rect(rect.left, rect.top, rect.left + rect.width() / getXClearance(), rect.top + rect.height() / getYClearance());

            int x1 = src.left;
            int y1 = src.top;

            int x2 = (int) x;
            int y2 = (int) y;

            Line2d line = new Line2d(x1, y1, x2, y2);

//        System.out.println("****Start*****" + x1 + " " + x2 + " " + tileWidth + " " + y1 + " " + y2);
            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
//        System.out.println(i + " " + j);
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
//                        System.out.println("Fucker1");
                            return true;
                        }
                    }
                }
            }
//        System.out.println("****End*****");

            x1 = src.right;
            y1 = src.top;

            x2 = x2 + src.width();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
//                        System.out.println("Fucker2");
                            return true;
                        }
                    }
                }
            }

            x1 = src.right;
            y1 = src.bottom;

            y2 = y2 + src.height();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = (Utils.min(x1, x2) + 3) / tileWidth; i <= (Utils.max(x1, x2) + 3) / tileWidth; i++){
                for(int j = (Utils.min(y1, y2) + 3) / tileHeight; j <= (Utils.max(y1, y2) + 3) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
//                        System.out.println("Fucker3");
                            return true;
                        }
                    }
                }
            }

//        for(int i = Utils.min(x1, x2); i <= Utils.max(x1, x2); i += 10){
//            int j = (int) line.getIntersectionPoint(Line2d.X_COORD, i)[1];
//            if(Utils.lineRectIntersection(line, new Rect(i / tileWidth * tileWidth, j / tileHeight * tileHeight,
//                    i / tileWidth * tileWidth + tileWidth, j / tileHeight * tileHeight + tileHeight))){
//                Node node = dungeonState.pathFindingMap.get(i / tileWidth, j / tileHeight);
//                if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
////                    System.out.println("Fucker3");
//                    return true;
//                }
//            }
//        }

            x1 = src.left;
            y1 = src.bottom;

            x2 = x2 - src.width();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
//                        System.out.println("Fucker4");
                            return true;
                        }
                    }
                }
            }

            return false;
        }else if(BattleState.getBattleState() instanceof ReplayState){
            ReplayState dungeonState = (ReplayState) BattleState.getBattleState();
            int tileWidth = dungeonState.tileCellWidth;
            int tileHeight = dungeonState.tileCellHeight;

            Rect src = new Rect(rect.left, rect.top, rect.left + rect.width() / getXClearance(), rect.top + rect.height() / getYClearance());

            int x1 = src.left;
            int y1 = src.top;

            int x2 = (int) x;
            int y2 = (int) y;

            Line2d line = new Line2d(x1, y1, x2, y2);

//        System.out.println("****Start*****" + x1 + " " + x2 + " " + tileWidth + " " + y1 + " " + y2);
            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
//        System.out.println(i + " " + j);
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
//                        System.out.println("Fucker1");
                            return true;
                        }
                    }
                }
            }
//        System.out.println("****End*****");

            x1 = src.right;
            y1 = src.top;

            x2 = x2 + src.width();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
//                        System.out.println("Fucker2");
                            return true;
                        }
                    }
                }
            }

            x1 = src.right;
            y1 = src.bottom;

            y2 = y2 + src.height();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = (Utils.min(x1, x2) + 3) / tileWidth; i <= (Utils.max(x1, x2) + 3) / tileWidth; i++){
                for(int j = (Utils.min(y1, y2) + 3) / tileHeight; j <= (Utils.max(y1, y2) + 3) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
//                        System.out.println("Fucker3");
                            return true;
                        }
                    }
                }
            }

//        for(int i = Utils.min(x1, x2); i <= Utils.max(x1, x2); i += 10){
//            int j = (int) line.getIntersectionPoint(Line2d.X_COORD, i)[1];
//            if(Utils.lineRectIntersection(line, new Rect(i / tileWidth * tileWidth, j / tileHeight * tileHeight,
//                    i / tileWidth * tileWidth + tileWidth, j / tileHeight * tileHeight + tileHeight))){
//                Node node = dungeonState.pathFindingMap.get(i / tileWidth, j / tileHeight);
//                if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
////                    System.out.println("Fucker3");
//                    return true;
//                }
//            }
//        }

            x1 = src.left;
            y1 = src.bottom;

            x2 = x2 - src.width();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
//                        System.out.println("Fucker4");
                            return true;
                        }
                    }
                }
            }

            return false;
        }else{
            return false;
        }
    }

    private boolean pathIntersected(Rect rect, int x2, int y2){
        if(BattleState.getBattleState() instanceof DungeonState){
            DungeonState dungeonState = (DungeonState) BattleState.getBattleState();
            int tileWidth = dungeonState.tileCellWidth;
            int tileHeight = dungeonState.tileCellHeight;

            Rect src = new Rect(rect.left, rect.top, rect.left + rect.width() / getXClearance(), rect.top + rect.height() / getYClearance());

            int x1 = src.left;
            int y1 = src.top;

            x2 = x2 * tileWidth;
            y2 = y2 * tileHeight;

            Line2d line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
                            return true;
                        }
                    }
                }
            }

            x1 = src.right;
            y1 = src.top;

            x2 = x2 + src.width();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
                            return true;
                        }
                    }
                }
            }

            x1 = src.right;
            y1 = src.bottom;

            y2 = y2 + src.height();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
                            return true;
                        }
                    }
                }
            }

            x1 = src.left;
            y1 = src.bottom;

            x2 = x2 - src.width();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
                            return true;
                        }
                    }
                }
            }

            return false;
        }else if(BattleState.getBattleState() instanceof ReplayState){
            ReplayState dungeonState = (ReplayState) BattleState.getBattleState();
            int tileWidth = dungeonState.tileCellWidth;
            int tileHeight = dungeonState.tileCellHeight;

            Rect src = new Rect(rect.left, rect.top, rect.left + rect.width() / getXClearance(), rect.top + rect.height() / getYClearance());

            int x1 = src.left;
            int y1 = src.top;

            x2 = x2 * tileWidth;
            y2 = y2 * tileHeight;

            Line2d line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
                            return true;
                        }
                    }
                }
            }

            x1 = src.right;
            y1 = src.top;

            x2 = x2 + src.width();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
                            return true;
                        }
                    }
                }
            }

            x1 = src.right;
            y1 = src.bottom;

            y2 = y2 + src.height();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
                            return true;
                        }
                    }
                }
            }

            x1 = src.left;
            y1 = src.bottom;

            x2 = x2 - src.width();

            line = new Line2d(x1, y1, x2, y2);

            for(int i = Utils.min(x1, x2) / tileWidth; i <= Utils.max(x1, x2) / tileWidth; i++){
                for(int j = Utils.min(y1, y2) / tileHeight; j <= Utils.max(y1, y2) / tileHeight; j++){
                    if(Utils.lineRectIntersection(line, new Rect(i * tileWidth, j * tileHeight, (i + 1) * tileWidth, (j + 1) * tileHeight))){
                        Node node = dungeonState.pathFindingMap.get(i, j);
                        if(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance()){
                            return true;
                        }
                    }
                }
            }

            return false;
        }else{
            return false;
        }
    }

    /**
     * Gives the move command to the unit.
     * @param x The x position to move to.
     * @param y The y position to move to.
     */
    public void commandMove(float x, float y){
        if(moveCd){
            return;
        }
//            pathObject = null;
        if(following){
            moveCd = true;
            new TimedTask(200) {
                @Override
                public void performAction() {
                    moveCd = false;
                }
            };
        }
        setX2(x);
        setY2(y);
        commands.clear();
        commands.add(Consts.STATE_MOVE);
        setState(Consts.STATE_MOVE);
        findPath();
    }

    public void commandCast(CastAnimation animation){
        commands.clear();
        setAnimation(animation);
        commands.add(Consts.STATE_CAST);
        setState(Consts.STATE_CAST);
    }

    public void commandAttack(Unit target){
        setTarget(target);
        commands.clear();
        commands.add(Consts.STATE_MOVE);
        commands.add(Consts.STATE_ATTACK);
        setState(Consts.STATE_MOVE);
    }

    /**
     * Destroys the unit. Removes any touch commands that started from the unit. Removes the destination of any touch commands that end with the unit.
     * Nullifies the target of all the attackers. Removes the unit from the units list.
     */
    @Override
    public void destroyEntity() {
        for(TouchHandler x : TouchHandler.getTouchHandlers()){
            if(x.getSrc() == this){
                TouchHandler.removeTouchHandler(x);
            }
            if(x.getDst() == this){
                x.setDst(null);
            }
        }
        for(Unit x : attackers){
            x.resetCommands();
        }
        for(Unit x : followers){
            x.setFollow(false, null, 0);
        }
        if(BattleState.getBattleState().getSelectedPc() == this) BattleState.getBattleState().setSelectedPc(null);
        destroyUnit();
        removeUnit(this);
    }

    public void updateSolidBox(){
        solidBox.set((int) getX(), getCollisionBox().top + getCollisionBox().height() * 3 / 4, (int) (getCollisionBox().width() + getX()), getCollisionBox().bottom);
    }

    /**
     * Ticks the unit. If target is dead makes it null.
     * Updates collision box based on the new position.
     * Handles the current state of the unit, performing the necessary actions.
     */
    @Override
    public void tickEntity() {
        updateSolidBox();
//        if(target != null) if(!target.doesExist()) setTarget(null);
        tickUnit();
        if(commands.size() > 0){
            switch(commands.get(0)){
                case Consts.STATE_MOVE :
                    if(onGuard){
                        if(getTarget() == null){
                            if(isInRangeOfGuardPoint(x2 + getCollisionBox().width() / 2, y2 + getCollisionBox().height() / 2)){
                                generalMove();
                            }else{
                                initGuardMovement();
                            }
                        }else{
                            if(isInRangeOfGuardPoint(getTarget().getX() + getCollisionBox().width() / 2, getTarget().getY() + getCollisionBox().height() / 2)){
                                generalMove();
                            }else{
                                setTarget(null);
                                initGuardMovement();
                            }
                        }
                    }else if(following){
                        if(getTarget() == null){
                            if(isInRangeOfFollowingUnit(x2 + getCollisionBox().width() / 2, y2 + getCollisionBox().height() / 2)){
                                if(this instanceof Priest){
//                                        System.out.println("Here1.");
//                                        System.out.println(commands.get(0));
                                }
                                generalMove();
                            }else{
                                if(this instanceof Priest){
//                                        System.out.println("Here2.");
//                                        System.out.println(commands.get(0));
                                }
                                initFollowMovement();
                            }
                        }else{
                            if(isInRangeOfFollowingUnit((getTarget().getX() + getCollisionBox().width() / 2 + followingUnit.getCollisionBox().centerX()) / 2, (getTarget().getY() + getCollisionBox().height() / 2 + followingUnit.getCollisionBox().centerY()) / 2)){

                                if(this instanceof Priest){
//                                        System.out.println("Here3.");
//                                        System.out.println(commands.get(0));
                                }generalMove();
                            }else{
                                if(this instanceof Priest){
//                                        System.out.println("Here4.");
//                                        System.out.println(commands.get(0));
                                }
                                setTarget(null);
                                initFollowMovement();
                            }
                        }
                    }else{
                        generalMove();
                    }
                    break;

                case Consts.STATE_ATTACK :
                    attack();
                    break;
            }
        }else if(following){
            if(!isInRangeOfFollowingUnit(getCollisionBox().centerX(), getCollisionBox().centerY())) initFollowMovement();
        }else if(onGuard){
            if(BattleState.getBattleState().isInCamera(this))
                if(!isInRangeOfGuardPoint(getCollisionBox().centerX(), getCollisionBox().centerY())) initGuardMovement();
        }
    }

    private boolean isInRangeOfFollowingUnit(float x, float y){
        return Utils.distance(x, y, followingUnit.getCollisionBox().centerX(), followingUnit.getCollisionBox().centerY())
                <= followRadius;
    }

    private void initFollowMovement(){
        if(BattleState.getBattleState() instanceof DungeonState){
            DungeonState dungeonState = (DungeonState) BattleState.getBattleState();
            int xTile, yTile;
            Node node;
            do{
                setX2(Utils.getRandomIntegerInTheRange((int) (followingUnit.getCollisionBox().centerX() - followRadius), (int) (followingUnit.getCollisionBox().centerX() + followRadius), null));
                setY2(Utils.getRandomIntegerInTheRange((int) (followingUnit.getCollisionBox().centerY() - followRadius / Consts.DISTANCE_FACTOR), (int) (followingUnit.getCollisionBox().centerY() + followRadius / Consts.DISTANCE_FACTOR), null));
                xTile = (int) x2 / dungeonState.tileCellWidth;
                yTile = (int) y2 / dungeonState.tileCellHeight;
                node = dungeonState.pathFindingMap.get(xTile, yTile);
            }while(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance());
        }else if(BattleState.getBattleState() instanceof ReplayState){
            ReplayState dungeonState = (ReplayState) BattleState.getBattleState();
            int xTile, yTile;
            Node node;
            do{
                setX2(Utils.getRandomIntegerInTheRange((int) (followingUnit.getCollisionBox().centerX() - followRadius), (int) (followingUnit.getCollisionBox().centerX() + followRadius), null));
                setY2(Utils.getRandomIntegerInTheRange((int) (followingUnit.getCollisionBox().centerY() - followRadius / Consts.DISTANCE_FACTOR), (int) (followingUnit.getCollisionBox().centerY() + followRadius / Consts.DISTANCE_FACTOR), null));
                xTile = (int) x2 / dungeonState.tileCellWidth;
                yTile = (int) y2 / dungeonState.tileCellHeight;
                node = dungeonState.pathFindingMap.get(xTile, yTile);
            }while(node == null || node.xClearance < getXClearance() || node.yClearance < getYClearance());
        }else{
            setX2(Utils.getRandomIntegerInTheRange((int) (followingUnit.getCollisionBox().centerX() - followRadius), (int) (followingUnit.getCollisionBox().centerX() + followRadius), null));
            setY2(Utils.getRandomIntegerInTheRange((int) (followingUnit.getCollisionBox().centerY() - followRadius / Consts.DISTANCE_FACTOR), (int) (followingUnit.getCollisionBox().centerY() + followRadius / Consts.DISTANCE_FACTOR), null));
        }
        commandMove(x2, y2);
    }

    private boolean isInRangeOfGuardPoint(float x, float y){
        return Utils.distance(x, y, guardX, guardY) < guardRadius;
    }

    private void initGuardMovement(){
        commandMove(guardX, guardY);
    }

    private void generalMove(){
        if(getTarget() == null){
            move();
        }else{
            if(target.doesExist()){
                if(isInAttackRange(getTarget())){
                    commands.remove(0);
                    setState(Consts.STATE_HOLD);
                    return;
                }
                if(initTargetLocation()){
                    findPath();
                    move();
                }else{
                    resetCommands();
                }
            }else{
                resetCommands();
            }
        }
    }

//    private int t1, t2;

    private void findPath(){
        if(BattleState.getBattleState() instanceof DungeonState){
            final DungeonState dungeonState = (DungeonState) BattleState.getBattleState();
            final float tileWidth = dungeonState.tileCellWidth;
            final float tileHeight = dungeonState.tileCellHeight;
            final float endSolidBoxX = x2 + tileWidth / 2;
            final float endSolidBoxY = y2 + getCollisionBox().height() - solidBox.height() + tileHeight / 2;
            final float finalX2 = x2;
            final float finalY2 = y2;
            final Unit self = this;
//            t1 = (int) (endSolidBoxX / tileWidth);
//            t2 = (int) (endSolidBoxY / tileHeight);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Object[] tempPath = PathFinding.findPath((int) (solidBox.left / tileWidth), (int) (solidBox.top / tileHeight),
                            (int) (endSolidBoxX / tileWidth), (int) (endSolidBoxY / tileHeight), dungeonState.pathFindingMap, Integer.MAX_VALUE, self);
                    if(tempPath == null) return;
                    if(dungeonState.getSelectedPc() == self){
                        if(x2 == finalX2 && y2 == finalY2){
                            pathObject = tempPath;
                        }
                    }else{
                        pathObject = tempPath;
                    }
                    if(!dungeonState.canMove(new Rect((int) x2, (int) y2 + getCollisionBox().height() - solidBox.height(), (int) x2 + solidBox.width(), (int) (y2 + getCollisionBox().height())), getXClearance(), getYClearance())){
                        //noinspection unchecked
                        ArrayList<int[]> path = (ArrayList<int[]>) tempPath[0];
                        int xTile = path.get(path.size() - 1)[0];
                        int yTile = path.get(path.size() - 1)[1];
                        setX2(xTile * tileWidth);
                        setY2(yTile * tileHeight + solidBox.height() - getCollisionBox().height());
                    }
                }
            }).start();
        }else if(BattleState.getBattleState() instanceof ReplayState){
            final ReplayState dungeonState = (ReplayState) BattleState.getBattleState();
            final float tileWidth = dungeonState.tileCellWidth;
            final float tileHeight = dungeonState.tileCellHeight;
            final float endSolidBoxX = x2 + tileWidth / 2;
            final float endSolidBoxY = y2 + getCollisionBox().height() - solidBox.height() + tileHeight / 2;
            final float finalX2 = x2;
            final float finalY2 = y2;
            final Unit self = this;
//            t1 = (int) (endSolidBoxX / tileWidth);
//            t2 = (int) (endSolidBoxY / tileHeight);
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                    Object[] tempPath = PathFinding.findPath((int) (solidBox.left / tileWidth), (int) (solidBox.top / tileHeight),
                            (int) (endSolidBoxX / tileWidth), (int) (endSolidBoxY / tileHeight), dungeonState.pathFindingMap, Integer.MAX_VALUE, self);
                    if(tempPath == null) return;
                    if(dungeonState.getSelectedPc() == self){
                        if(x2 == finalX2 && y2 == finalY2){
                            pathObject = tempPath;
                        }
                    }else{
                        pathObject = tempPath;
                    }
                    if(!dungeonState.canMove(new Rect((int) x2, (int) y2 + getCollisionBox().height() - solidBox.height(), (int) x2 + solidBox.width(), (int) (y2 + getCollisionBox().height())), getXClearance(), getYClearance())){
                        //noinspection unchecked
                        ArrayList<int[]> path = (ArrayList<int[]>) tempPath[0];
                        int xTile = path.get(path.size() - 1)[0];
                        int yTile = path.get(path.size() - 1)[1];
                        setX2(xTile * tileWidth);
                        setY2(yTile * tileHeight + solidBox.height() - getCollisionBox().height());
                    }
                 }
             }).start();
        }
    }

    /**
     * Has the attack go on cooldown. As in you can't attack unless the cooldown time is passed from the time the method was called.
     * Is called on every hit.
     */
    public void attackCD(){
        new TimedTask(attackCD) {
            @Override
            public void performAction() {
                setCanAttack(true);
            }
        };
    }

    /**
     * Damages the unit. Calls the destroy() method if hp drops to 0 or less.
     * @param damage The damage number.
     * @param attacker The attacker.
     */
    public void damage(float damage, Unit attacker){
        if(!(BattleState.getBattleState() instanceof ReplayState)){
            if(BattleState.getBattleState() instanceof DungeonState){
                DungeonState.getDungeonState().replayDamage(this, damage, attacker);
            }
        }else{
            return;
        }

        if(!getVulnerability()) return;
        if(damage > 0) damage = Utils.getReducedDamage(damage, armor);
        setHp(hp - damage);
        new DamageVisualEffect(damage, getCollisionBox().centerX(), getY() - 4 * GameView.density());
        if(hp <= 0){
            if(GameView.getState() instanceof DungeonState) KillQuest.unitDied(this.getClass());
            destroy();
        }else reactToBeingAttacked(attacker);
    }

    public void damageReplay(float damage, Unit attacker){
        try{
            if(!getVulnerability()) return;
            if(damage > 0) damage = Utils.getReducedDamage(damage, armor);
            setHp(hp - damage);
            new DamageVisualEffect(damage, getCollisionBox().centerX(), getY() - 4 * GameView.density());
            if(hp <= 0){
                if(GameView.getState() instanceof DungeonState) KillQuest.unitDied(this.getClass());
                destroy();
            }else reactToBeingAttacked(attacker);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Adds the unit to the units list.
     */
    private static void addUnit(final Unit unit){
        new Event() {
            @Override
            public void performAction() {
                getUnits().add(unit);
            }
        };
    }

    /**
     * Removes the unit from the units list.
     */
    private static void removeUnit(final Unit unit){
        new Event() {
            @Override
            public void performAction() {
                    units.remove(unit);
            }
        };
    }

    /**
     * Considers the passed in unit as an attacker to this unit for later considerations.
     * @param unit The attacker.
     */
    private void addAttacker(final Unit unit){
        if(attackers.contains(unit)) return;
        attackers.add(unit);
    }

    private void addFollower(final Unit unit){
        if(followers.contains(unit)) return;
        followers.add(unit);
    }

    /**
     * Removes the unit from this units attackers list.
     */
    private void removeAttacker(final Unit unit){
        new Event() {
            @Override
            public void performAction() {
                attackers.remove(unit);
            }
        };
    }

    private void removeFollower(final Unit unit){
        new Event() {
            @Override
            public void performAction() {
                followers.remove(unit);
            }
        };
    }

    /**
     * Adds all the given spells to the unit.
     */
    public void loadSpells(ArrayList<Spell> list){
        list.addAll(spells);
    }

    /**
     * Renders the unit.
     * Draws the health bar.
     */
    @Override
    public void renderEntity(float xOffset, float yOffset) {
        renderUnit(xOffset, yOffset);
//        Paint paint = new Paint();
//        canvas.drawRect(Utils.getTranslatedRect(solidBox, xOffset, yOffset), paint);
//        paint.setStrokeWidth(4 * GameView.density());
//        canvas.drawLine(0, 0, x2 + xOffset, y2 + yOffset, paint);
//        paint.setColor(Color.RED);
//        canvas.drawRect(t1 * 64 + xOffset, t2 * 64 + yOffset, t1 * 64 + 64 + xOffset, t2 * 64 + 64 + yOffset, paint);
//        paint.setColor(Color.GREEN);
//        canvas.drawRect(x2 + xOffset, y2 + getCollisionBox().height() - solidBox.height() + yOffset, x2 + solidBox.width() + xOffset, y2 + getCollisionBox().height() + yOffset, paint);
    }

    /**
     * @return A lock that controls the synchronization of the actions done on the units list.
     */
    public static Object getUnitsLock() {
        return unitsLock;
    }

    /**
     * @return An ArrayList of all the existing units.
     */
    public static ArrayList<Unit> getUnits() {
        return units;
    }

    /**
     * Sets the hp of the unit. It will not exceed the max hp of the unit.
     */
    public void setHp(float hp) {
        this.hp = Utils.min(hp, maxHp);
    }

    /**
     * Sets the x2 coordinate of the unit. The x position that the unit will move towards.
     */
    public void setX2(float x2) {
        this.x2 = x2;
    }

    /**
     * Sets the y2 coordinate of the unit. The y position that the unit will move towards.
     */
    public void setY2(float y2) {
        this.y2 = y2;
    }

    /**
     * @return The side/team/faction the unit belongs to.
     */
    public byte getTeam() {
        return team;
    }

    /**
     * Sets the side/team/faction of the unit.
     */
    private void setTeam(byte team) {
        this.team = team;
    }

    public void resetCommands(){
        setTarget(null);
        commands.clear();
        setState(Consts.STATE_HOLD);
    }

    /**
     * Updates the state of the unit.
     * If the state is switching from attack state the target wil be nullified.
     * If the state is switching to hold state the animation will pause and a static image is shown instead.
     */
    public void setState(byte state){
        if(state == this.state) return;
        switch(this.state){
            case Consts.STATE_ATTACK :

                break;
        }
        switch(state){
            case Consts.STATE_HOLD :
                if(this instanceof Knight){
                    if(getAnimation() == moveLeftAnimation){
                        setAnimation(idleLeftAnimation);
                    }else if(getAnimation() == moveRightAnimation){
                        setAnimation(idleRightAnimation);
                    }
                }else{
                    if(getAnimation() == moveLeftAnimation || getAnimation() == moveRightAnimation){
                        setImage(getAnimation());
                    }
                }
                break;

            case Consts.STATE_MOVE :
//                pathObject = null;
                break;
        }
        this.state = state;
    }

    /**
     * Sets the target of the unit.
     * Adds this unit to the attackers list of the target.
     * Removes this unit from the attackers list of its last target.
     */
    public void setTarget(Unit target) {
        if(target != null) target.addAttacker(this); else if(getState() == Consts.STATE_ATTACK) setState(Consts.STATE_HOLD);
        if(this.target != null) this.target.removeAttacker(this);
        this.target = target;
    }

    /**
     * Adds the spell to the unit.
     */
    protected void addSpell(final Spell spell){
        new Event() {
            @Override
            public void performAction() {
                spells.add(spell);
            }
        };
    }

    /**
     * Sets the x position if valid move.
     */
    @Override
    public boolean setX(float x){
        if(BattleState.getBattleState().canMove(new Rect((int) x, solidBox.top, (int) (x + solidBox.width()), solidBox.bottom), getXClearance(), getYClearance())){
            return super.setX(x);
        }else{
            return false;
        }
    }

    /**
     * Sets the y position if valid move.
     */
    @Override
    public boolean setY(float y){
        if(BattleState.getBattleState().canMove(new Rect(solidBox.left, (int) (y + getCollisionBox().height() - solidBox.height()), solidBox.right, (int) (y + getCollisionBox().height())), getXClearance(), getYClearance())){
            return super.setY(y);
        }else{
            return false;
        }
    }

    /**
     * Controls the attacking capability of the unit.
     */
    private void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    /**
     * @return The current hp of the unit.
     */
    public float getHp() {
        return hp;
    }

    /**
     * @return The max hp of the unit.
     */
    public float getMaxHp() {
        return maxHp;
    }

    /**
     * Sets the maxHp of the unit.
     */
    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    /**
     * @return The attack damage of the unit.
     */
    public float getDamage() {
        return damage;
    }

    /**
     * @return The target of the unit.
     */
    public Unit getTarget() {
        return target;
    }

    /**
     * Sets the attack damage of the unit.
     */
    public void setDamage(float damage) {
        this.damage = damage;
    }

    /**
     * @return The state of the unit.
     */
    public byte getState() {
        return state;
    }

    /**
     * @return The movement speed of the unit.
     */
    public float getSpeed() {
        return Utils.min(Consts.MAXIMUM_SPEED, Utils.max(Consts.MINIMUM_SPEED, speed)) * (State.slowMo ? 0.33f : 1);
    }

    public float getRealSpeed(){
        return speed;
    }

    /**
     * Sets the movement speed of the unit.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Sets the right attack animation of the unit.
     */
    protected void setAttackRightAnimation(Animation attackRightAnimation) {
        this.attackRightAnimation = attackRightAnimation;
    }

    /**
     * Sets the left attack animation of the unit.
     */
    protected void setAttackLeftAnimation(Animation attackLeftAnimation) {
        this.attackLeftAnimation = attackLeftAnimation;
    }

    /**
     * @return The right move animation of the unit.
     */
    public Animation getMoveRightAnimation() {
        return moveRightAnimation;
    }

    /**
     * Sets the right move animation of the unit.
     */
    protected void setMoveRightAnimation(Animation moveRightAnimation) {
        this.moveRightAnimation = moveRightAnimation;
    }

    /**
     * @return The left move animation of the unit.
     */
    public Animation getMoveLeftAnimation() {
        return moveLeftAnimation;
    }

    /**
     * Sets the left move animation of the unit.
     */
    protected void setMoveLeftAnimation(Animation moveLeftAnimation) {
        this.moveLeftAnimation = moveLeftAnimation;
    }

    /**
     * Sets the attack cooldown of the unit. Is in milliseconds.
     */
    protected void setAttackCD(int attackCD) {
        this.attackCD = attackCD;
    }

    /**
     * @return The attack range of the unit.
     */
    public float getAttackRange() {
        return attackRange;
    }

    /**
     * Sets the attack range of the unit.
     */
    protected void setAttackRange(float attackRange) {
        this.attackRange = attackRange;
    }

    /**
     * Sets the aggro range of the unit.
     */
    protected void setAggroRange(int range){
        this.aggroRange = range;
    }

    /**
     * @return The aggro range of the unit.
     */
    protected int getAggroRange(){
        return aggroRange;
    }

    /**
     * @return The armor.
     */
    public float getArmor() {
        return armor;
    }

    /**
     * Sets the armor.
     */
    public void setArmor(float armor) {
        this.armor = armor;
    }

    public boolean isOnGuard() {
        return onGuard;
    }

    public float getGuardRadius() {
        return guardRadius;
    }

    public float getGuardX() {
        return guardX;
    }

    public float getGuardY() {
        return guardY;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    protected void setProjectileSpeed(float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
    }

    public Rect getSolidBox() {
        return solidBox;
    }

    public HealthBarRenderer getHealthBar() {
        return healthBar;
    }

    public void setFollow(boolean flag, Unit unit, float radius) {
        if(followingUnit != null){
            followingUnit.removeFollower(this);
        }
        if(unit != null) {
            unit.addFollower(this);
        }
        following = flag;
        followingUnit = unit;
        followRadius = radius;
    }

    private boolean getVulnerability() {
        return vulnerability;
    }

    protected void setVulnerability(boolean vulnerability) {
        this.vulnerability = vulnerability;
    }

    public boolean getShowHealthBar() {
        return showHealthBar;
    }

    protected void setShowHealthBar(boolean showHealthBar) {
        this.showHealthBar = showHealthBar;
    }

    public int getXClearance(){
        if(BattleState.getBattleState() instanceof DungeonState){
            return solidBox.width() / DungeonState.getDungeonState().tileCellWidth + 1;
        }else if(BattleState.getBattleState() instanceof ReplayState) {
            return solidBox.width() / ReplayState.getReplayState().tileCellWidth + 1;
        }else{
            return 0;
        }
    }

    public int getYClearance(){
        if(BattleState.getBattleState() instanceof DungeonState){
            return solidBox.height() / DungeonState.getDungeonState().tileCellHeight + 1;
        }else if(BattleState.getBattleState() instanceof ReplayState) {
            return solidBox.height() / ReplayState.getReplayState().tileCellHeight + 1;
        }else{
            return 0;
        }
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public boolean setPosition(float x, float y) {
        if(BattleState.getBattleState().canMove(new Rect((int) x, (int) y + getCollisionBox().height() - solidBox.height(), (int) (x + solidBox.width()), (int) y + getCollisionBox().height()), getXClearance(), getYClearance())){
            return super.setX(x) && super.setY(y);
        }else{
            return false;
        }
    }

    protected Animation getIdleLeftAnimation() {
        return idleLeftAnimation;
    }

    protected void setIdleLeftAnimation(Animation idleLeftAnimation) {
        this.idleLeftAnimation = idleLeftAnimation;
    }

    protected Animation getIdleRightAnimation() {
        return idleRightAnimation;
    }

    protected void setIdleRightAnimation(Animation idleRightAnimation) {
        this.idleRightAnimation = idleRightAnimation;
    }
}