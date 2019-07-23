package com.tinslam.battleheart.items.armors;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.items.Armor;
import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class for the Simple shield item.
 */
public class SimpleShield extends Armor{
    /**
     * Constructor.
     * @param carrier If no carrier input "".
     */
    public SimpleShield(String carrier) {
        super(carrier);

        name = GameView.string(R.string.simple_shield);
        description = GameView.string(R.string.simple_shield_description);

        image = TextureData.item_simple_shield;

        characters.add(NameConsts.KNIGHT);
        characters.add(NameConsts.ARCHER);

        price = 20;

        hp = 13;
        armor = 1;

        properties.add(GameView.string(R.string.hp) + " : +" + hp);
        properties.add(GameView.string(R.string.armor) + " : +" + armor);
    }
}
