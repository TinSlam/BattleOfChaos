package com.tinslam.battleheart.items.boots;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.items.Boots;
import com.tinslam.battleheart.utils.constants.NameConsts;

/**
 * A class for the Light threads item.
 */
public class LightThreads extends Boots{
    /**
     * Constructor.
     * @param carrier If no carrier input "".
     */
    public LightThreads(String carrier) {
        super(carrier);

        name = GameView.string(R.string.light_threads);
        description = GameView.string(R.string.light_threads_description);

        image = TextureData.item_light_threads;

        characters.add(NameConsts.KNIGHT);
        characters.add(NameConsts.ARCHER);
        characters.add(NameConsts.PRIEST);

        price = 40;

        attackCd = 5;
        speed = 2;

        properties.add(GameView.string(R.string.speed) + " : +" + speed);
        properties.add(GameView.string(R.string.attack_speed) + " : +" + attackCd + "%");
    }
}
