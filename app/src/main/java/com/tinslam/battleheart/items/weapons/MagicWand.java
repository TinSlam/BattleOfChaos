package com.tinslam.battleheart.items.weapons;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.items.Weapon;
import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class for the Magic wand item.
 */
public class MagicWand extends Weapon{
    /**
     * Constructor.
     * @param carrier If no carrier input "".
     */
    public MagicWand(String carrier) {
        super(carrier);

        name = GameView.string(R.string.magic_wand);
        description = GameView.string(R.string.magic_wand_description);

        image = TextureData.item_magic_wand;

        price = 20;

        characters.add(NameConsts.PRIEST);

        damage = -2;
        attackRange = 10;

        properties.add(GameView.string(R.string.heal) + " : +" + -damage);
        properties.add(GameView.string(R.string.cast_range) + " : +" + attackRange);
    }
}
