package com.tinslam.battleheart.entities.entities3D;

import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.GameActivity;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.Model3D;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity3D;
import com.tinslam.battleheart.states.ArmoryState;

/**
 * The armory building which takes you to the Armory.
 */
public class Armory extends Entity3D{
    /**
     * Constructor.
     */
    public Armory(float x, float y, float z, float rotX, float rotY, float rotZ, float width, float height, float depth) {
        super(x, y, z, rotX, rotY, rotZ, width, height, depth);

        setClickable(true);
    }

    /**
     * Is called onUp.
     */
    @Override
    public void onActionUp() {
        if(GameView.stateChangeOnCD) return;
        ActivityManager.switchToActivity(OpenGLActivity.openGLActivity, OpenGL2dActivity.class);
        GameView.setState(new ArmoryState(), "");
    }

    /**
     * Loads the 3D model.
     */
    @Override
    public void loadModel() {
        setModelData(Model3D.block);
        setTexture(TextureData.orangeBox);
    }
}