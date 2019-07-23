package com.tinslam.battleheart.entities.entities3D;

import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity3D;
import com.tinslam.battleheart.elements3D.Model3D;

/**
 * A class that handles all the existing grounds :D
 */
public class Ground extends Entity3D {
    /**
     * Constructor.
     */
    public Ground(float x, float y, float z, float rotX, float rotY, float rotZ, float width, float height, float depth) {
        super(x, y, z, rotX, rotY, rotZ, width, height, depth);
    }

    /**
     * Is called when touched.
     */
    @Override
    public void onActionUp() {

    }

    /**
     * Is called as soon as it's created. Sets the model and the initial texture.
     */
    @Override
    public void loadModel(){
        setModelData(Model3D.block);
        setTexture(TextureData.grass);
    }
}
