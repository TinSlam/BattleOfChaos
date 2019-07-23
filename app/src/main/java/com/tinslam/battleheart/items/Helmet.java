package com.tinslam.battleheart.items;

import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class that holds all the Helmets.
 */
public abstract class Helmet extends Item{
    public Helmet(String carrier) {
        super(carrier);

        type = NameConsts.ITEM_HELMET;
    }
}
