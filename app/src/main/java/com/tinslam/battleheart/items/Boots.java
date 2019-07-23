package com.tinslam.battleheart.items;

import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class that holds all Boots.
 */
public abstract class Boots extends Item{
    public Boots(String carrier) {
        super(carrier);

        type = NameConsts.ITEM_BOOTS;
    }
}
