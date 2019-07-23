package com.tinslam.battleheart.items.armors;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.items.Armor;
import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class for the GiantShield item.
 */
public class GiantShield extends Armor{
    /**
     * Constructor.
     * @param carrier Input "" if no carrier.
     */
    public GiantShield(String carrier) {
        super(carrier);

        name = GameView.string(R.string.giant_shield);
        description = GameView.string(R.string.giant_shield_description);

        image = TextureData.item_giant_shield;

        characters.add(NameConsts.KNIGHT);

        price = 120;

        armor = 10;
        speed = -3;

        properties.add(GameView.string(R.string.armor) + " : +" + armor);
        properties.add(GameView.string(R.string.speed) + " : " + speed);
    }
}
