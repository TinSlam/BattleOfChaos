package com.tinslam.battleheart.UI.graphics.visualEffects.attachedToUnitVisualEffects;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.visualEffects.VisualEffect;
import com.tinslam.battleheart.entities.units.Unit;

/**
 * A class that hold all the visual effects attached to some unit.
 */
public abstract class VisualEffectAttachedToUnit extends VisualEffect {
    protected Unit unit;

    /**
     * Constructor.
     */
    VisualEffectAttachedToUnit(Unit unit, Animation animation, float x, float y){
        super(animation, x, y);

        this.unit = unit;
    }

    /**
     * Updates the x position.
     */
    public abstract float updateX(Unit unit);

    /**
     * Updates the y position.
     */
    public abstract float updateY(Unit unit);

    /**
     * Is called on every frame.
     */
    public abstract void tickVisualEffectAttachedToUnit();

    /**
     * Is called on every frame.
     */
    @Override
    public void tickVisualEffect() {
        if(unit == null || !unit.doesExist()){
            removeVisualEffect(this);
            return;
        }
        setX(updateX(unit));
        setY(updateY(unit));
        tickVisualEffectAttachedToUnit();
    }
}
