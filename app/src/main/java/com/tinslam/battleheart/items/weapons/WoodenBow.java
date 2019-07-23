package com.tinslam.battleheart.items.weapons;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.items.Weapon;
import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class for the Wooden bow item.
 */
public class WoodenBow extends Weapon{
    /**
     * Constructor.
     * @param carrier If no carrier input "".
     */
    public WoodenBow(String carrier) {
        super(carrier);

        name = GameView.string(R.string.wooden_bow);
        description = GameView.string(R.string.wooden_bow_description);

        image = TextureData.item_wooden_bow;

        characters.add(NameConsts.ARCHER);

        price = 30;

        damage = 2;
        attackRange = 30;

        properties.add(GameView.string(R.string.damage) + " : +" + damage);
        properties.add(GameView.string(R.string.attack_range) + " : +" + attackRange);
    }
}
