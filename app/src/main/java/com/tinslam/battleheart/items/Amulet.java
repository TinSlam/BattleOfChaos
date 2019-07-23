package com.tinslam.battleheart.items;

import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class that holds all the Amulets.
 */
public abstract class Amulet extends Item{
    public Amulet(String carrier) {
        super(carrier);

        type = NameConsts.ITEM_AMULET;
    }
}
