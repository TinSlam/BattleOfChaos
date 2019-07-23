package com.tinslam.battleheart.items;

import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class that holds all the Weapons.
 */
public abstract class Weapon extends Item{
    public Weapon(String carrier) {
        super(carrier);

        type = NameConsts.ITEM_WEAPON;
    }
}
