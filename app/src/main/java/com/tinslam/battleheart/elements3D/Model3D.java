package com.tinslam.battleheart.elements3D;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.base.ModelData;

/**
 * A class that hold all the 3D models.
 */
public class Model3D{
    public static ModelData block, stall;

    /**
     * Loads the models.
     */
    public static void loadModels(){
        if(block == null) block = new ModelData(R.raw.block); else {block.clear(); block = new ModelData(R.raw.block);}
        if(stall == null) stall = new ModelData(R.raw.stall); else {stall.clear(); stall = new ModelData(R.raw.stall);}
    }
}
