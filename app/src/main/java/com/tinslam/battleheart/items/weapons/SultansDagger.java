package com.tinslam.battleheart.items.weapons;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.items.Weapon;
import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class for the Sultan's dagger item.
 */
public class SultansDagger extends Weapon{
    /**
     * Constructor.
     * @param carrier If no carrier input "".
     */
    public SultansDagger(String carrier) {
        super(carrier);

        name = GameView.string(R.string.sultans_dagger);
        description = GameView.string(R.string.sultans_dagger_description);

        image = TextureData.item_sultans_dagger;

        characters.add(NameConsts.NINJA);

        price = 30;

        damage = 3;
        attackCd = 3;

        properties.add(GameView.string(R.string.damage) + " : +" + damage);
        properties.add(GameView.string(R.string.attack_speed) + " : +" + attackCd + "%");
    }
}
