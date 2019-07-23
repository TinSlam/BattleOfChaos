package com.tinslam.battleheart.items;

import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class that holds all the Armors.
 */
public abstract class Armor extends Item{
    public Armor(String carrier) {
        super(carrier);

        type = NameConsts.ITEM_ARMOR;
    }
}
