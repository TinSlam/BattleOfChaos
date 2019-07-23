package com.tinslam.battleheart.gameUtility;

import android.graphics.BlurMaskFilter;
import android.graphics.Paint;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;

/**
 * A class that handles touch inputs.
 */
public class TouchHandler{
    private Pc src = null;
    private Unit dst = null;
    private int x2, y2;
    private long initialTime;
    public static Paint paint = new Paint(), blurPaint = new Paint();
    private int pointerId = -1;
    private static final Object touchHandlersLock = new Object();
    private static ArrayList<TouchHandler> touchHandlers = new ArrayList<>();

    static{
        paint.setARGB(255, 255, 255, 255);
        blurPaint.setARGB(255, 0, 255, 0);
        paint.setStrokeWidth(8 * GameView.density());
        blurPaint.setStrokeWidth(8 * GameView.density());
        blurPaint.setMaskFilter(new BlurMaskFilter(4 * GameView.density(), BlurMaskFilter.Blur.NORMAL));
    }

    /**
     * Constructor.
     * @param src The Pc that is going to perform an action.
     * @param pointerId The id that keeps track of which finger is handling the event.
     */
    public TouchHandler(Pc src, int pointerId){
        this.pointerId = pointerId;
        this.src = src;
        this.x2 = (int) (src.getX() + src.getCollisionBox().width() / 2);
        this.y2 = src.getCollisionBox().bottom - src.getCollisionBox().width() / 5;
        initialTime = System.nanoTime();
        for(TouchHandler x : touchHandlers){
            if(x.getPointerId() == pointerId){
                return;
            }
        }
        addTouchHandler(this);
    }

    /**
     * The action that is performed once the finger is released.
     * Makes the source Pc move to a location or assigns it a target and calls perform action for the unit.
     * Removes the input event at the end.
     */
    public void perform(){
         if(dst == null){
             if(BattleState.getBattleState() instanceof DungeonState){
                 DungeonState.getDungeonState().replayCommandMove(src, (float) x2 - src.getCollisionBox().width() / 2, (float) y2 - src.getCollisionBox().height());
             }
             BattleState.getBattleState().queueEvent(new Object[] {"move", src, (float) x2 - src.getCollisionBox().width() / 2, (float) y2 - src.getCollisionBox().height()});
         }else{
             if(BattleState.getBattleState() instanceof DungeonState){
                 DungeonState.getDungeonState().replayCommandAttack(src, dst);
             }
             BattleState.getBattleState().queueEvent(new Object[] {"attack", src, dst});
         }
         removeTouchHandler(this);
    }

    public boolean isValid(){
        float distance;
        if(dst != null){
            distance = Utils.distance(src, dst);
        }else{
            distance = Utils.distance(src, x2, y2);
        }
        return System.nanoTime() - initialTime > 150000000 || distance > 48 * GameView.density();
    }

    /**
     *
     * @return The touch input that is associated with the given id.
     */
    public static TouchHandler getTouchHandler(int pointerId){
        for(TouchHandler x : touchHandlers){
            if(x.getPointerId() == pointerId){
                return x;
            }
        }

        return null;
    }

    /**
     * Removes the touch input.
     */
    public static void removeTouchHandler(final TouchHandler th){
        new Event() {
            @Override
            public void performAction() {
                    touchHandlers.remove(th);
            }
        };
    }

    /**
     * Adds the touch input to the list.
     */
    private static void addTouchHandler(final TouchHandler th){
        new Event() {
            @Override
            public void performAction() {
                    touchHandlers.add(th);
            }
        };
    }

    /**
     * Renders the touch input.
     */
    private void renderTouchHandler(float xOffset, float yOffset){
        if(!isValid()) return;
        if(dst != null) MyGL2dRenderer.drawLabel(dst.getCollisionBox().left - 4 * GameView.density() + xOffset,
                dst.getCollisionBox().bottom - dst.getCollisionBox().width() / 5 + yOffset,
                dst.getCollisionBox().width() + 8 * GameView.density(),
                dst.getCollisionBox().width() * 2 / 5,
                TextureData.selected_character_visual, 255);
        else MyGL2dRenderer.drawLabel(x2 - src.getCollisionBox().width() / 2 - 4 * GameView.density() + xOffset,
                y2 - src.getCollisionBox().width() / 5 + yOffset,
                src.getCollisionBox().width() + 8 * GameView.density(),
                src.getCollisionBox().width() * 2 / 5,
                TextureData.selected_character_visual, 255);

        MyGL2dRenderer.drawLabel(src.getCollisionBox().left - 4 * GameView.density() + xOffset,
                src.getCollisionBox().bottom - src.getCollisionBox().width() / 5 + yOffset,
                src.getCollisionBox().width() + 8 * GameView.density(),
                src.getCollisionBox().width() * 2 / 5,
                TextureData.selected_character_visual, 255);

        if(dst != null) MyGL2dRenderer.drawLabelRotationAtStart(TextureData.solid_white, 255, new float[]{
                src.getCollisionBox().centerX() + xOffset,
                src.getCollisionBox().bottom - 4 * GameView.density() + yOffset,
                Utils.distance(src.getCollisionBox().centerX(), src.getCollisionBox().bottom,
                        dst.getCollisionBox().centerX(), dst.getCollisionBox().bottom),
                8 * GameView.density(),
                (float) Math.toDegrees(Math.atan2(dst.getCollisionBox().bottom - src.getCollisionBox().bottom,
                        dst.getCollisionBox().centerX() - src.getCollisionBox().centerX()))
        });
        else MyGL2dRenderer.drawLabelRotationAtStart(TextureData.solid_white, 255, new float[]{
                src.getCollisionBox().centerX() + xOffset,
                src.getCollisionBox().bottom - 4 * GameView.density() + yOffset,
                Utils.distance(src.getCollisionBox().centerX(), src.getCollisionBox().bottom,
                        x2, y2),
                8 * GameView.density(),
                (float) Math.toDegrees(Math.atan2(y2 - src.getCollisionBox().bottom,
                        x2 - src.getCollisionBox().centerX()))
            });
    }

    /**
     * Removes all the touch inputs. Is called at the end of a state.
     */
    public static void clear(){
        new Event() {
            @Override
            public void performAction() {
                touchHandlers.clear();
            }
        };
    }

    /**
     * Calls render for all touch inputs.
     */
    public static void render(float xOffset, float yOffset){
        for(TouchHandler x : touchHandlers){
            x.renderTouchHandler(xOffset, yOffset);
        }
    }

    /**
     *
     * @return The source Pc of the touch input.
     */
    public Pc getSrc() {
        return src;
    }

    /**
     *
     * @return The destination unit of the touch input.
     */
    public Unit getDst() {
        return dst;
    }

    /**
     * Sets the destination unit of the touch input.
     */
    public void setDst(Unit dst) {
        this.dst = dst;
    }

    /**
     *
     * @return The id associated with the input. Used to determine which finger is handling the event.
     */
    private int getPointerId() {
        return pointerId;
    }

    /**
     *
     * @return A lock that controls the synchronization of the actions performed on the touchHandlers list.
     */
    public static Object getTouchHandlersLock() {
        return touchHandlersLock;
    }

    /**
     *
     * @return An ArrayList of all the touch inputs.
     */
    public static ArrayList<TouchHandler> getTouchHandlers() {
        return touchHandlers;
    }

    /**
     * Sets the x position of the destination of the touch input.
     */
    public void setX2(int x2) {
        this.x2 = x2;
    }

    /**
     * Sets the y position of the destination of the touch input.
     */
    public void setY2(int y2) {
        this.y2 = y2;
    }
}
