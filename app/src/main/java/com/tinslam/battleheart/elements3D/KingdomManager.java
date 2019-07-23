package com.tinslam.battleheart.elements3D;

import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.GameActivity;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.entities.Entity3D;
import com.tinslam.battleheart.entities.entities3D.Ground;
import com.tinslam.battleheart.states.ArmoryState;
import com.tinslam.battleheart.states.DungeonState;
import com.tinslam.battleheart.states.ShopState;
import com.tinslam.battleheart.states.CastleState;
import com.tinslam.battleheart.utils.FileManager;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.FileConsts;

/**
 * A class that handles the 3D Kingdom state.
 */
public class KingdomManager{
    private static Entity3D selectedEntity = null;
    public static boolean[][] walkable;
    private static boolean immerseMode = false;
    private static boolean varyingLighting = false;
    public static float ambientLighting = 0.7f;

    /**
     * Sets an entity as the selected entity.
     */
    public static void selectEntity(Entity3D entity3D){
        if(entity3D == selectedEntity){
            selectedEntity = null;
        }else{
            selectedEntity = entity3D;
        }
    }

    /**
     * Loads the kingdom. Buildings and UI.
     */
    public static void loadKingdom(){
        new Label(-0.1f, 0.75f, 0.2f, 0.25f, TextureData.color_yellow_3d){
            @Override
            public boolean onUp() {
                if(immerseMode){
                    setTexture(TextureData.color_yellow_3d);
                    setImmerseMode(false);
                    MyGLRenderer.resetCameraPosition();
                }else{
                    setTexture(TextureData.color_blue_3d);
                    setImmerseMode(true);
                    MyGLRenderer.setCameraPositionToImmerseMode();
                }
                return true;
            }

            @Override
            public boolean onDown() {
                return true;
            }
        };
        new Label(0.1f, 0.75f, 0.2f, 0.25f, TextureData.color_yellow_3d){
            @Override
            public boolean onUp() {
                if(varyingLighting){
                    setTexture(TextureData.color_yellow_3d);
                    setVaryingLighting(false);
                }else{
                    setTexture(TextureData.color_blue_3d);
                    setVaryingLighting(true);
                }
                return true;
            }

            @Override
            public boolean onDown() {
                return true;
            }
        };
        new Label(0.5f, 0.85f, 0.5f, 0.15f, TextureData.greenBox) {
            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                ActivityManager.switchToActivity(OpenGLActivity.openGLActivity, OpenGL2dActivity.class);
                GameView.setState(new DungeonState(), "");
                return true;
            }

            @Override
            public boolean onDown() {
                return true;
            }
        };
        new Label(-1, 0.7f, 0.3f, 0.3f, TextureData.greenBox) {
            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                ActivityManager.switchToActivity(OpenGLActivity.openGLActivity, OpenGL2dActivity.class);
                GameView.setState(new ArmoryState(), "");
                return true;
            }

            @Override
            public boolean onDown() {
                return false;
            }
        };
        new Label(0.8f, -1f, 0.2f, 0.3f, TextureData.greenBox) {
            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                ActivityManager.switchToActivity(OpenGLActivity.openGLActivity, OpenGL2dActivity.class);
                GameView.setState(new ShopState(), "");
                return true;
            }

            @Override
            public boolean onDown() {
                return false;
            }
        };
        new Label(-1, -1f, 1.6f, 0.3f, TextureData.greenBox) {
            @Override
            public boolean onUp() {
                if(GameView.stateChangeOnCD) return true;
                ActivityManager.switchToActivity(OpenGLActivity.openGLActivity, OpenGL2dActivity.class);
                GameView.setState(new CastleState(), "");
                return true;
            }

            @Override
            public boolean onDown() {
                return false;
            }
        };

        new Ground(0, 0, 0f, 0, 0, 0, 64, 64, 1);

        FileManager.loadKingdom(FileConsts.CASTLE_LAYOUT_FILE_NAME, MyGL2dRenderer.getContext());
    }

    public static boolean isEmpty(float x1, float x2, float y1, float y2){
        for(int i = (int) x1; i < x2; i++){
            for(int j = (int) y1; j < y2; j++){
                if(!walkable[i][j]) return false;
            }
        }

        return true;
    }

    public static boolean isXEmpty(float x1, float x2, float y){
        for(int i = (int) x1; i < x2; i++){
            if(!walkable[i][(int) y]) return false;
        }

        return true;
    }

    public static boolean isYEmpty(float y1, float y2, float x){
        for(int i = (int) y1; i < y2; i++){
            if(!walkable[(int) x][i]) return false;
        }

        return true;
    }

    public static boolean isImmerseMode(){
        return immerseMode;
    }

    private static void setImmerseMode(boolean immerseMode){
        KingdomManager.immerseMode = immerseMode;
    }

    private static void setVaryingLighting(boolean varyingLighting){
        KingdomManager.varyingLighting = varyingLighting;
        if(varyingLighting){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        while(ambientLighting > 0){
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!KingdomManager.varyingLighting) return;
                            ambientLighting -= 0.005f;
                        }
                        ambientLighting = 0;
                        while(ambientLighting < 1){
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!KingdomManager.varyingLighting) return;
                            ambientLighting += 0.005f;
                        }
                    }
                }
            }).start();
        }else{
            ambientLighting = 0.7f;
        }
    }
}