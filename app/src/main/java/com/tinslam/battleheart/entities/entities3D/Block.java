package com.tinslam.battleheart.entities.entities3D;

import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity3D;
import com.tinslam.battleheart.elements3D.KingdomManager;
import com.tinslam.battleheart.elements3D.Model3D;

/**
 * A class that contains all the existing blocks.
 */
public class Block extends Entity3D {

    /**
     * Constructor.
     */
    public Block(float x, float y, float z, float rotX, float rotY, float rotZ, float width, float height, float depth) {
        super(x, y, z, rotX, rotY, rotZ, width, height, depth);
    }

    /**
     * Is called when clicked.
     */
    @Override
    public void onActionUp() {
        KingdomManager.selectEntity(this);
    }

    /**
     * Is called as soon as it's created. Sets the model and the initial texture.
     */
    @Override
    public void loadModel(){
        setModelData(Model3D.block);
        setTexture(TextureData.crate);
    }
}
