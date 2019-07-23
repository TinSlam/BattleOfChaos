package com.tinslam.battleheart.UI.graphics.renderingAssistants;

import android.graphics.Rect;

import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.utils.constants.Consts;

import java.util.ArrayList;

/**
 * The class that handles rendering health bars.
 */
public class HealthBarRenderer{
    private static ArrayList<HealthBarRenderer> healthBarRenderers = new ArrayList<>();
    private static final Object healthBarRenderersLock = new Object();

    private Unit unit;
    private int texture;

    /**
     * Constructor.
     * @param unit The unit to draw the health bar for.
     */
    public HealthBarRenderer(Unit unit){
        this.unit = unit;

        if(unit.getTeam() == Consts.TEAM_ALLIED) texture = TextureData.solid_green;
        else texture = TextureData.solid_yellow;

        addHealthBarRenderer(this);
    }

    /**
     * Renders the health bar with the given offset.
     */
    public void renderHealthBar(float xOffset, float yOffset){
        if(unit.getHp() <= 0) return;
        MyGL2dRenderer.drawLabel(unit.getCollisionBox().centerX() - 16 * GameView.density() + xOffset,
                unit.getCollisionBox().top - 12 * GameView.density() + yOffset,
                32 * GameView.density(),
                6 * GameView.density(),
                TextureData.solid_black,
                255);
        MyGL2dRenderer.drawLabel(unit.getCollisionBox().centerX() - 16 * GameView.density() + xOffset,
                unit.getCollisionBox().top - 12 * GameView.density() + yOffset,
                - 16 * GameView.density() + (32 * GameView.density()) * unit.getHp() / unit.getMaxHp() - (- 16 * GameView.density()),
                6 * GameView.density(),
                texture,
                255);
    }

    /**
     * Renders the health bar at the given rectangle.
     */
    public void render(Rect rect){
        MyGL2dRenderer.drawLabel(rect.left, rect.top, rect.width(), rect.height(), TextureData.solid_black, 255);
        MyGL2dRenderer.drawLabel(rect.left, rect.top, rect.width() * (unit.getHp() / unit.getMaxHp()), rect.height(), texture, 255);
    }

    /**
     * Clears all health bar renderers.
     */
    public static void clear(){
        healthBarRenderers.clear();
    }

    public static boolean isEmpty(){
        return healthBarRenderers.isEmpty();
    }

    /**
     * Renders all health bars.
     */
    public static void render(float xOffset, float yOffset){
        for(HealthBarRenderer x : healthBarRenderers){
            if(x.unit == null || !x.unit.doesExist()){
                removeHealthBarRenderer(x);
                continue;
            }
            if(x.unit.getShowHealthBar()) x.renderHealthBar(xOffset, yOffset);
        }
    }

    /**
     * Adds health bar to the list of all health bars.
     */
    private static void addHealthBarRenderer(final HealthBarRenderer hbr){
        new Event() {
            @Override
            public void performAction() {
                healthBarRenderers.add(hbr);
            }
        };
    }

    /**
     * Removes health bar.
     */
    private static void removeHealthBarRenderer(final HealthBarRenderer hbr){
        new Event() {
            @Override
            public void performAction() {
                healthBarRenderers.remove(hbr);
            }
        };
    }

    public void setForegroundTexture(int foregroundTexture) {
        texture = foregroundTexture;
    }
}
