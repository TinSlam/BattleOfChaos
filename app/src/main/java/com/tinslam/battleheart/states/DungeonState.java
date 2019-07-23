package com.tinslam.battleheart.states;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.os.Build;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.buttons.NullButton;
import com.tinslam.battleheart.UI.buttons.roundbuttons.RoundButton;
import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.HealthBarRenderer;
import com.tinslam.battleheart.UI.graphics.visualEffects.VisualEffect;
import com.tinslam.battleheart.activities.ActivityManager;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.activities.OpenGLActivity;
import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.entities.Sign;
import com.tinslam.battleheart.entities.collectables.Collectable;
import com.tinslam.battleheart.entities.projectiles.Projectile;
import com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses.Boss;
import com.tinslam.battleheart.entities.units.NPCs.neutralNPCs.QuestNpc;
import com.tinslam.battleheart.entities.units.PCs.Archer;
import com.tinslam.battleheart.entities.units.PCs.Knight;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.PCs.Priest;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.Node;
import com.tinslam.battleheart.gameUtility.PathFindingMap;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.gameUtility.PositionOutsideCameraIndicator;
import com.tinslam.battleheart.gameUtility.SpawnCamp;
import com.tinslam.battleheart.gameUtility.TouchHandler;
import com.tinslam.battleheart.quests.Quest;
import com.tinslam.battleheart.spells.Spell;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.FileConsts;
import com.tinslam.battleheart.utils.constants.NameConsts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class DungeonState extends BattleState{
    @SuppressWarnings("unchecked")
    private int mapWidth = 100, mapHeight = 100;
    @SuppressWarnings("unchecked")
    private ArrayList<Integer>[][] tiles = new ArrayList[mapWidth][mapHeight];
    public PathFindingMap pathFindingMap = new PathFindingMap();
    public int tileCellWidth = (int) (32 * GameView.density());
    public int tileCellHeight = (int) (32 * GameView.density());
    @SuppressWarnings("FieldCanBeLocal")
    private int tiles1Start = 0, tiles2Start = 136, tiles3Start = 336, tiles4Start = 600, tiles5Start = 744, tiles6Start = 896, tiles7Start = 1120;
    private Rect cameraRect = new Rect();
    private Rect cameraLockArea = new Rect();
    private boolean lockCamera = false;
    private ArrayList<float[]> allySpawnPoints = new ArrayList<>();
    private boolean showingMessage = false;
//    private Animation weatherAnimation;
    private boolean followingDisabled = false, lastFollowState;

    private final Object positionIndicatorsLock = new Object();
    private ArrayList<PositionOutsideCameraIndicator> positionIndicators = new ArrayList<>();

    private Button followButton;

    private boolean bossFightStarted = false;
    private ArrayList<String> replayFile = new ArrayList<>();
    private int replayFramesPassed = 0;
    private int replayStateId = 0;

    private final Object questsLock = new Object();
    private ArrayList<Quest> quests = new ArrayList<>();
    private Quest currentQuest;
    private boolean showQuest = false;
    private boolean questShowAll = false;

    private ImageView questPanel;
    private int questPanelWidth = GameView.getScreenWidth() / 3;
    private int questPanelHeight = GameView.getScreenHeight() * 5 / 8;
    private int questsPanelXOffset = GameView.getScreenWidth() - questPanelWidth;
    private int questsPanelYOffset = GameView.getScreenHeight() / 8;

    private TextView questName;
    private ImageView questNamePanel;
    private int questNameBoxXOffset = questsPanelXOffset;
    private int questNameBoxYOffset = questsPanelYOffset;
    private int questNameBoxWidth = questPanelWidth * 3 / 5;
    private int questNameBoxHeight = questPanelHeight / 6;

    private ImageView questShowAllButton;
    private int questOtherQuestsXOffset = questNameBoxXOffset + questNameBoxWidth;
    private int questOtherQuestsYOffset = questsPanelYOffset;
    private int questOtherQuestsWidth = (questPanelWidth - questNameBoxWidth) / 2;
    private int questOtherQuestsHeight = questPanelHeight / 6;

    private ImageView questToggleButton;
    private int questToggleShowXOffset = questOtherQuestsXOffset + questOtherQuestsWidth;
    private int questToggleShowYOffset = questsPanelYOffset;
    private int questToggleShowWidth = (questPanelWidth - questNameBoxWidth) / 2;
    private int questToggleShowHeight = questPanelHeight / 6;

    private TextView questDescription;
    private ArrayList<TextView> questRewards = new ArrayList<>();
    private TextView questReward;

    private TextView questProgress;
    private ImageView questProgressPanel;
    private int questProgressXOffset = questsPanelXOffset;
    private int questProgressYOffset = questsPanelYOffset + questPanelHeight;
    private int questProgressWidth = questPanelWidth;
    private int questProgressHeight = questNameBoxHeight;

    private int maxQuests = 4;
    private Bitmap questImage = Image.resizeImage(Image.button_empty, questPanelWidth, (questPanelHeight - questNameBoxHeight) / maxQuests);
    private Bitmap selectedQuestImage = Image.resizeImage(Image.button_empty_hover, questPanelWidth, (questPanelHeight - questNameBoxHeight) / maxQuests);
    private ArrayList<ImageView> questActiveButtons = new ArrayList<>();
    private HashMap<ImageView, Boolean> questButtonActiveTracker = new HashMap<>();
    private HashMap<ImageView, TextView> questActivesTexts = new HashMap<>();

    private int[] vboId1 = new int[1];
    private int[] vboId2 = new int[1];
    private int[] iboId1 = new int[1];
    private int[] iboId2 = new int[1];
    private boolean vboCreated = false;

    private boolean cutScene = false;
    private int cutSceneAlpha = 0;
    private int cutSceneCounter = 0;

    private boolean shakeScreen = false;
    private int shakeCounter = 0;
    private int shakeFrames = 1;

    private TextView message;
    private ImageView messagePanel;

    private HashMap<Object, String> replayMap = new HashMap<>();

    @Override
    public void onBackPressed() {
        if(GameView.stateChangeOnCD) return;
        //noinspection unused
        GameView.getState().setConfirmation(true, new NullButton() {
            public void performOnUp(){
                State state = new DungeonSelectorState();
                GameView.setState(state, "", null);
//                GameView.setState(state, "", new LoadingState(state));
                ActivityManager.switchToActivity(OpenGL2dActivity.openGL2dActivity, OpenGLActivity.class);
            }
        }, null);
    }

    @Override
    public void lose(){
        // If this object is not the current arena state obsolete it.
        if(this != getDungeonState()) return;

        battleStateLose();
    }

    @Override
    public void pcDied(Pc pc) {
        disablePcPortrait(pc);
    }

    @Override
    public void initBackground() {

    }

    /**
     * Initializes the state.
     */
    @Override
    public void startBattleState() {
        LoadingState loadingState = new LoadingState(this);
        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.solid_black, 255);
        cameraRect = new Rect(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight());
//        weatherAnimation = new PerpetualAnimation(Utils.resizeAnimation(AnimationLoader.rain_2, GameView.getScreenWidth(), GameView.getScreenHeight()), 50, null);
//        weatherAnimation.reset();
        initQuests();
        initMap();
        loadPcs();
        initMessagePanel();
        Texture texture = new Texture(TextureData.color_yellow, (int) (32 * GameView.density()), (int) (32 * GameView.density()));
        followButton = new RoundButton((int) (GameView.getScreenWidth() - 24 * GameView.density()),
                (int) (GameView.getScreenHeight() - 24 * GameView.density()),
                texture,
                texture,
                true) {
            @Override
            public boolean onDown() {
                return !lockCamera;
            }

            @Override
            public boolean onUp() {
                if(lockCamera) return false;
                setFollowingDisabled(!isFollowingDisabled());
                if(isFollowingDisabled()){
                    image.setTexture(TextureData.color_blue);
                    imageOnClick.setTexture(TextureData.color_blue);
                }else{
                    image.setTexture(TextureData.color_yellow);
                    imageOnClick.setTexture(TextureData.color_yellow);
                }
                return true;
            }
        };
        loadingState.finishLoading();
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                questToggleButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void replaySpellClicked(Spell spell){
        if(!bossFightStarted) return;
        newReplayState();
        replayFile.add("click_button " + replayMap.get(spell));
    }

    public void replayShootPowershot(Spell spell, float x, float y){
        if(!bossFightStarted) return;
        newReplayState();
        replayFile.add("cast_powershot " + replayMap.get(spell) + " " + x + " " + y);
    }

    private void initMessagePanel(){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagePanel = new ImageView(OpenGL2dActivity.openGL2dActivity);
                message = new TextView(OpenGL2dActivity.openGL2dActivity);
                OpenGL2dActivity.openGL2dActivity.addContentView(messagePanel, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(message, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                messagePanel.setImageBitmap(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.light_brown_panel), GameView.getScreenWidth() * 8 / 10, GameView.getScreenHeight() / 6));
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = GameView.getScreenWidth() / 10;
                params.topMargin = GameView.getScreenHeight() / 12;
                params.width = GameView.getScreenWidth() * 8 / 10;
                params.height = GameView.getScreenHeight() / 6;
                messagePanel.setLayoutParams(params);
                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                    message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                params.leftMargin = GameView.getScreenWidth() / 10;
                params.topMargin = GameView.getScreenHeight() / 12 + GameView.getScreenHeight() / 12;
                params.width = GameView.getScreenWidth() * 8 / 10;
                params.height = GameView.getScreenHeight() / 6;
                message.setLayoutParams(params);
                message.setTextColor(Color.BLACK);
                message.setVisibility(View.GONE);
                messagePanel.setVisibility(View.GONE);
            }
        });
    }

    public void addQuest(final Quest quest){
        if(quests.size() == maxQuests) return;
        quests.add(quest);
        quest.setActive(true);
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ImageView button = new ImageView(OpenGL2dActivity.openGL2dActivity);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = 0;
                params.topMargin = 0;
                params.width = questPanelWidth;
                params.height = (questPanelHeight - questNameBoxHeight) / maxQuests;
                button.setLayoutParams(params);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(questButtonActiveTracker.get(button) == null ||
                                !questButtonActiveTracker.get(button)) return;
                        currentQuest = quests.get(questActiveButtons.indexOf(button));
                        for(int i = 0; i < questActiveButtons.size(); i++){
                            final ImageView rectangleButton = questActiveButtons.get(i);
                            final int finalI = i;
                            OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(quests.indexOf(currentQuest) != finalI) rectangleButton.setImageBitmap(questImage); else rectangleButton.setImageBitmap(selectedQuestImage);
                                }
                            });
                        }
                        updateQuest();
                        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                questName.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
                TextView textView = new TextView(OpenGL2dActivity.openGL2dActivity);
                textView.setText(quest.getName());
                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = 0;
                params.topMargin = 0;
                params.width = questPanelWidth;
                params.height = (questPanelHeight - questNameBoxHeight) / maxQuests;
                button.setLayoutParams(params);
                OpenGL2dActivity.openGL2dActivity.addContentView(button, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(textView, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                questActiveButtons.add(button);
                questButtonActiveTracker.put(button, questShowAll);
                questActivesTexts.put(button, textView);
                if(questShowAll && showQuest){
                    button.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }else{
                    button.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                }
                if(quests.size() == 1){
                    currentQuest = quests.get(0);
                    updateQuest();
                    if(showQuest){
                        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                questName.setVisibility(View.VISIBLE);
                            }
                        });
                        if(!questShowAll){
                            OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    questDescription.setVisibility(View.VISIBLE);
                                    questReward.setVisibility(View.VISIBLE);
                                    questProgress.setVisibility(View.VISIBLE);
                                    if(!questProgress.getText().toString().equalsIgnoreCase("")) questProgressPanel.setVisibility(View.VISIBLE);
                                    for(TextView tv : questRewards){
                                        tv.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                }
                for(int i = 0; i < questActiveButtons.size(); i++){
                    ImageView rectangleButton = questActiveButtons.get(i);
                    TextView tv = questActivesTexts.get(rectangleButton);
                    params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                    params.leftMargin = questsPanelXOffset;
                    params.topMargin = questNameBoxYOffset + questNameBoxHeight + i * (questPanelHeight - questNameBoxHeight) / maxQuests;
                    params.width = questPanelWidth;
                    params.height = (questPanelHeight - questNameBoxHeight) / maxQuests;
                    button.setLayoutParams(params);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                    tv.setTextSize((questPanelHeight - questNameBoxHeight) / maxQuests / 3 / GameView.density());
                    tv.measure(0, 0);
                    int textHeight = tv.getMeasuredHeight();
                    params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                    params.leftMargin = questsPanelXOffset;
                    params.topMargin = questNameBoxYOffset + questNameBoxHeight + i * (questPanelHeight - questNameBoxHeight) / maxQuests + textHeight / 2;
                    params.width = questPanelWidth;
                    tv.setLayoutParams(params);
                    if(quests.indexOf(currentQuest) != i) rectangleButton.setImageBitmap(questImage); else rectangleButton.setImageBitmap(selectedQuestImage);
                }
            }
        });
    }

    public void removeQuest(final Quest quest){
        quest.setActive(false);
        if(currentQuest == quest) currentQuest = null;
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = quests.indexOf(quest);
                ImageView button = questActiveButtons.get(index);
                button.setVisibility(View.GONE);
                questActivesTexts.get(button).setVisibility(View.GONE);
                questButtonActiveTracker.remove(button);
                questActivesTexts.remove(button);
                questActiveButtons.remove(index);
                quests.remove(quest);
                for(int i = index; i < questActiveButtons.size(); i++){
                    button = questActiveButtons.get(index);
                    TextView tv = questActivesTexts.get(button);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                    params.leftMargin = questsPanelXOffset;
                    params.topMargin = questNameBoxYOffset + questNameBoxHeight + i * (questPanelHeight - questNameBoxHeight) / maxQuests;
                    params.width = questPanelWidth;
                    params.height = (questPanelHeight - questNameBoxHeight) / maxQuests;
                    button.setLayoutParams(params);

                    tv.measure(0, 0);
                    int textHeight = tv.getMeasuredHeight();
                    params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                    params.leftMargin = questsPanelXOffset;
                    params.topMargin = questNameBoxYOffset + questNameBoxHeight + i * (questPanelHeight - questNameBoxHeight) / maxQuests + textHeight / 2;
                    params.width = questPanelWidth;
                    tv.setLayoutParams(params);
                }
            }
        });
        updateQuest();
    }

    public void receiveRewards(Quest quest){
        quest.receiveRewards();
    }

    private void updateQuest(){
        String name = GameView.string(R.string.no_quest);
        String description = GameView.string(R.string.non_available);
        final String rewardString = GameView.string(R.string.rewards) + " : ";
        String progress = "";
        ArrayList<String> rewards = new ArrayList<>();
        if(currentQuest != null){
            name = currentQuest.getName();
            description = currentQuest.getDescription();
            rewards = currentQuest.getRewards();
            progress = currentQuest.getProgress();
        }

        final String finalName = name;
        final String finalDescription = description;
        final ArrayList<String> finalRewards1 = rewards;
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                questName.setText(finalName);
                setTextSizeDynamically(questName, questNameBoxXOffset + questNameBoxWidth / 10,
                        questNameBoxYOffset + questNameBoxHeight / 2,
                        questNameBoxWidth * 8 / 10,
                        questNameBoxHeight);

                questDescription.setText(finalDescription);
                questDescription.setTextSize(questPanelHeight / 18 / GameView.density());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = questsPanelXOffset + questPanelWidth / 20;
                params.topMargin = questNameBoxYOffset + questNameBoxHeight + questPanelHeight / 30;
                params.width = questPanelWidth * 9 / 10;
                questDescription.setLayoutParams(params);

                if(finalRewards1.isEmpty()){
                    questReward.setText("");
                }else{
                    questReward.setText(rewardString);
                }
            }
        });

        updateQuestProgressTextRenderer(progress);

        final ArrayList<String> finalRewards = rewards;
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(TextView tv : questRewards){
                    tv.setText("");
                }
                for(int i = 0; i < finalRewards.size(); i++){
                    questRewards.get(0).setText(finalRewards.get(i));
                }
            }
        });
    }

    public void updateQuestProgressTextRenderer(final String progress){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                questProgress.setText(progress);
                setTextSizeDynamically(questProgress, questProgressXOffset + questProgressWidth / 10,
                        questProgressYOffset + questProgressHeight / 2,
                        questProgressWidth * 8 / 10,
                        questProgressHeight);
                if(showQuest && !questShowAll){
                    if(!progress.equalsIgnoreCase("")) questProgressPanel.setVisibility(View.VISIBLE);
                    questProgress.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initQuests(){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                questPanel = new ImageView(OpenGL2dActivity.openGL2dActivity);
                questNamePanel = new ImageView(OpenGL2dActivity.openGL2dActivity);
                questShowAllButton = new ImageView(OpenGL2dActivity.openGL2dActivity);
                questToggleButton = new ImageView(OpenGL2dActivity.openGL2dActivity);
                questProgressPanel = new ImageView(OpenGL2dActivity.openGL2dActivity);
                questName = new TextView(OpenGL2dActivity.openGL2dActivity);
                questDescription = new TextView(OpenGL2dActivity.openGL2dActivity);
                questReward = new TextView(OpenGL2dActivity.openGL2dActivity);
                questProgress = new TextView(OpenGL2dActivity.openGL2dActivity);
                for(int i = 0; i < 4; i++){
                    questRewards.add(new TextView(OpenGL2dActivity.openGL2dActivity));
                }

                OpenGL2dActivity.openGL2dActivity.addContentView(questPanel, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(questNamePanel, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(questShowAllButton, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(questToggleButton, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(questProgressPanel, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(questName, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(questDescription, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(questReward, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(questProgress, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                for(TextView tv : questRewards){
                    OpenGL2dActivity.openGL2dActivity.addContentView(tv, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                }

                questPanel.setImageBitmap(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.quest_panel), questPanelWidth, questPanelHeight));
                questNamePanel.setImageBitmap(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.quest_name_box), questNameBoxWidth, questNameBoxHeight));
                questShowAllButton.setImageBitmap(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.quest_more_button), questOtherQuestsWidth, questOtherQuestsHeight));
                final Bitmap image = Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.blue_arrow_right), questToggleShowWidth, questToggleShowHeight);
                final Bitmap flippedImage = Image.flipHorizontally(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.blue_arrow_right), questToggleShowWidth, questToggleShowHeight));
                questToggleButton.setImageBitmap(flippedImage);
                questProgressPanel.setImageBitmap(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.light_brown_panel), questProgressWidth, questProgressHeight));

                questName.setTextColor(Color.BLACK);
                questDescription.setTextColor(Color.BLACK);
                questReward.setTextColor(Color.BLACK);
                for(TextView tv : questRewards){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                    tv.setTextColor(Color.BLACK);
                }
                questProgress.setTextColor(Color.BLACK);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = questsPanelXOffset;
                params.topMargin = questsPanelYOffset;
                params.width = questPanelWidth;
                params.height = questPanelHeight;
                questPanel.setLayoutParams(params);

                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = questNameBoxXOffset;
                params.topMargin = questNameBoxYOffset;
                params.width = questNameBoxWidth;
                params.height = questNameBoxHeight;
                questNamePanel.setLayoutParams(params);

                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = questToggleShowXOffset;
                params.topMargin = questToggleShowYOffset;
                params.width = questToggleShowWidth;
                params.height = questToggleShowHeight;
                questToggleButton.setLayoutParams(params);

                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = questOtherQuestsXOffset;
                params.topMargin = questOtherQuestsYOffset;
                params.width = questOtherQuestsWidth;
                params.height = questOtherQuestsHeight;
                questShowAllButton.setLayoutParams(params);

                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = questProgressXOffset;
                params.topMargin = questProgressYOffset;
                params.width = questProgressWidth;
                params.height = questProgressHeight;
                questProgressPanel.setLayoutParams(params);

                setTextSizeDynamically(questName, questNameBoxXOffset + questNameBoxWidth / 10,
                        questNameBoxYOffset + questNameBoxHeight / 2,
                        questNameBoxWidth * 8 / 10,
                        questNameBoxHeight);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    questDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                }

                questDescription.setTextSize(questPanelHeight / 18 / GameView.density());
                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = questsPanelXOffset + questPanelWidth / 20;
                params.topMargin = questNameBoxYOffset + questNameBoxHeight + questPanelHeight / 30;
                params.width = questPanelWidth * 9 / 10;
                questDescription.setLayoutParams(params);

                questReward.setText(GameView.string(R.string.rewards));
                setTextSizeDynamically(questReward, questsPanelXOffset + questPanelWidth / 20,
                        questsPanelYOffset + questPanelHeight * 2 / 3 + questPanelHeight / 6 / 2,
                        questPanelWidth * 9 / 10,
                        questPanelHeight / 6);

                setTextSizeDynamically(questProgress, questProgressXOffset + questProgressWidth / 10,
                        questProgressYOffset + questProgressHeight / 2,
                        questProgressWidth * 8 / 10,
                        questProgressHeight);

                int rewardsPerLine = 2;
                int i = 0;
                for(TextView tv : questRewards){
                    setTextSizeDynamically(tv, questsPanelXOffset + questPanelWidth / rewardsPerLine * (i % rewardsPerLine) + questPanelWidth / 20,
                            questsPanelYOffset + questPanelHeight * 3 / 4 + questPanelHeight / 24 + questPanelHeight / 8 * (i / rewardsPerLine),
                            questPanelWidth / rewardsPerLine - questPanelWidth / 10,
                            questPanelHeight / 20 * 3);
                    i++;
                }

                questPanel.setVisibility(View.GONE);
                questNamePanel.setVisibility(View.GONE);
                questShowAllButton.setVisibility(View.GONE);
                questToggleButton.setVisibility(View.GONE);
                questProgressPanel.setVisibility(View.GONE);
                questName.setVisibility(View.GONE);
                questDescription.setVisibility(View.GONE);
                questReward.setVisibility(View.GONE);
                questProgress.setVisibility(View.GONE);
                for(TextView tv : questRewards){
                    tv.setVisibility(View.GONE);
                }

                questToggleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(lockCamera) return;
                        showQuest = !showQuest;
                        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(showQuest){
                                    questToggleButton.setImageBitmap(image);
                                }else{
                                    questToggleButton.setImageBitmap(flippedImage);
                                }
                                if(showQuest){
                                    questPanel.setVisibility(View.VISIBLE);
                                    questNamePanel.setVisibility(View.VISIBLE);
                                    questShowAllButton.setVisibility(View.VISIBLE);
                                }else{
                                    questPanel.setVisibility(View.GONE);
                                    questNamePanel.setVisibility(View.GONE);
                                    questShowAllButton.setVisibility(View.GONE);
                                }
                            }
                        });
                        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!showQuest){
                                    for(ImageView x : questActiveButtons){
                                        TextView tv = questActivesTexts.get(x);
                                        questButtonActiveTracker.put(x, false);
                                        x.setVisibility(View.GONE);
                                        tv.setVisibility(View.GONE);
                                    }
                                    OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            questName.setVisibility(View.GONE);
                                            questDescription.setVisibility(View.GONE);
                                            questReward.setVisibility(View.GONE);
                                            questProgress.setVisibility(View.GONE);
                                            questProgressPanel.setVisibility(View.GONE);
                                            for(TextView tv : questRewards){
                                                tv.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }else if(questShowAll){
                                    for(ImageView x : questActiveButtons){
                                        TextView tv = questActivesTexts.get(x);
                                        questButtonActiveTracker.put(x, true);
                                        x.setVisibility(View.VISIBLE);
                                        tv.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                        if(showQuest){
                            OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    questName.setVisibility(View.VISIBLE);
                                    if(showQuest && !questShowAll){
                                        questDescription.setVisibility(View.VISIBLE);
                                        questReward.setVisibility(View.VISIBLE);
                                        questProgress.setVisibility(View.VISIBLE);
                                        if(!questProgress.getText().toString().equalsIgnoreCase("")) questProgressPanel.setVisibility(View.VISIBLE);
                                        for(TextView tv : questRewards){
                                            tv.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });

                questShowAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!showQuest) return;
                        questShowAll = !questShowAll;
                        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(ImageView button : questActiveButtons){
                                    TextView tv = questActivesTexts.get(button);
                                    questButtonActiveTracker.put(button, questShowAll);
                                    if(questShowAll){
                                        button.setVisibility(View.VISIBLE);
                                        tv.setVisibility(View.VISIBLE);
                                    }else{
                                        button.setVisibility(View.GONE);
                                        tv.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                        if(!questShowAll){
                            OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    questDescription.setVisibility(View.VISIBLE);
                                    questReward.setVisibility(View.VISIBLE);
                                    questProgress.setVisibility(View.VISIBLE);
                                    if(!questProgress.getText().toString().equalsIgnoreCase("")) questProgressPanel.setVisibility(View.VISIBLE);
                                    for(TextView tv : questRewards){
                                        tv.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }else{
                            OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    questDescription.setVisibility(View.GONE);
                                    questReward.setVisibility(View.GONE);
                                    questProgress.setVisibility(View.GONE);
                                    questProgressPanel.setVisibility(View.GONE);
                                    for(TextView tv : questRewards){
                                        tv.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        updateQuest();
    }

    private void setTextSizeDynamically(TextView tv, int x, int y, int maxWidth, int height){
        if(tv == null || tv.getPaint() == null) return;
        tv.measure(0, 0);
        int width;
        float size = height / 3 / GameView.density();
        int i = 0;
        Rect bounds = new Rect();
        do{
            tv.setTextSize(size - 0.5f * i);
            tv.getPaint().getTextBounds(tv.getText().toString(), 0, tv.getText().toString().length(), bounds);
            width = bounds.width();
            i++;
        }while(width > maxWidth);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
        params.leftMargin = x;
        params.topMargin = (int) (y - bounds.height() + tv.getPaint().descent() / 2);
        params.width = maxWidth;
        tv.setLayoutParams(params);
    }

    private void initMap(){
        try {
            InputStreamReader isr = new InputStreamReader(GameView.Context().getAssets().open("dungeon1_lowest_level"));
            BufferedReader br = new BufferedReader(isr);

            String line;
            String[] tokens;
            for(int j = 0; j < mapHeight; j++){
                line = br.readLine();
                tokens = line.split(",");
                for(int i = 0; i < mapWidth; i++){
                    tiles[i][j] = new ArrayList<>();
                    tiles[i][j].add(Integer.parseInt(tokens[i]) - 1);
                }
            }

            br.close();

            isr = new InputStreamReader(GameView.Context().getAssets().open("dungeon1_tile_layer1"));
            br = new BufferedReader(isr);

            for(int j = 0; j < mapHeight; j++){
                line = br.readLine();
                tokens = line.split(",");
                for(int i = 0; i < mapWidth; i++){
                    tiles[i][j].add(Integer.parseInt(tokens[i]) - 1);
                }
            }

            br.close();

            isr = new InputStreamReader(GameView.Context().getAssets().open("dungeon1_walkable"));
            br = new BufferedReader(isr);

            for(int j = 0; j < mapHeight; j++){
                line = br.readLine();
                tokens = line.split(",");
                for(int i = 0; i < mapWidth; i++){
                    if(Integer.parseInt(tokens[i]) - 1 != -1){
                        pathFindingMap.addWalkable(i, j);
                    }
                }
            }

            br.close();

            isr = new InputStreamReader(GameView.Context().getAssets().open("dungeon1_units_layer"));
            br = new BufferedReader(isr);

            line = br.readLine();
            while(line != null && !line.isEmpty()){
                if(line.startsWith("<object")){
                    tokens = line.split(" ");
                    String type = "";
                    float x = 0, y = 0;
                    for(int i = 1; i < tokens.length; i++){
                        if(tokens[i].startsWith("name=")) continue;
                        String[] tempTokens = tokens[i].split("=");
                        tempTokens[1] = tempTokens[1].substring(1, tempTokens[1].lastIndexOf('"'));
                        switch(tempTokens[0]){
                            case "type" :
                                    type = tempTokens[1];
                                break;

                            case "x" :
                                    x = Float.parseFloat(tempTokens[1]) * GameView.density();
                                break;

                            case "y" :
                                    y = Float.parseFloat(tempTokens[1]) * GameView.density();
                                break;
                        }
                    }

                    int xTile = (int) (x / 32 / GameView.density());
                    int yTile = (int) (y / 32 / GameView.density());
                    if(type.startsWith("remove")){
                        String[] typeTokens = type.split("/");
                        if(PlayerStats.getLastLevelUnlocked() > Integer.parseInt(typeTokens[2])){
                            switch(typeTokens[1]){
                                case "tile_layer1" :
                                    tiles[xTile][yTile].set(1, -1);
                                    break;
                            }
                            if(typeTokens[3].equalsIgnoreCase("walkable")){
                                pathFindingMap.addWalkable(xTile, yTile);
                            }
                        }
                    }else if(type.startsWith("sign")){
                        String[] typeTokens = type.split("/");
                        Sign sign = new Sign(xTile * tileCellWidth, yTile * tileCellHeight);
                        int level = Integer.parseInt(typeTokens[2]);
                        if(PlayerStats.getLastLevelUnlocked() > level){
                            sign.setText("Path already unlocked by beating level " + level + " in the Arena.");
                        }else{
                            sign.setText("A new path will be unlocked by beating level " + level + " in the Arena.");
                        }
                    }else if(type.startsWith("replace")){
                        String[] typeTokens = type.split("/");
                        int level = Integer.parseInt(typeTokens[4]);
                        if(PlayerStats.getLastLevelUnlocked() > level){
                            int index = Integer.parseInt(typeTokens[2]);
                            switch(typeTokens[3]){
                                case "tileset1" :
                                    index += tiles1Start;
                                    break;

                                case "tileset2" :
                                    index += tiles2Start;
                                    break;

                                case "tileset3" :
                                    index += tiles3Start;
                                    break;

                                case "tileset4" :
                                    index += tiles4Start;
                                    break;

                                case "tileset5" :
                                    index += tiles5Start;
                                    break;

                                case "tileset6" :
                                    index += tiles6Start;
                                    break;

                                case "tileset7" :
                                    index += tiles7Start;
                                    break;
                            }
                            int layerIndex = 0;
                            switch(typeTokens[1]){
                                case "tile_layer1" :
                                    layerIndex = 1;
                                    break;
                            }
                            tiles[xTile][yTile].set(layerIndex, index);
                            if(typeTokens[5].equalsIgnoreCase("walkable")) pathFindingMap.addWalkable(xTile, yTile);
                        }
                    }else if(type.startsWith("spawn_camp")){
                        String[] typeTokens = type.split("/");
                        String[] classNames = typeTokens[1].split("_");
                        Class[] classes = new Class[classNames.length];
                        for(int i = 0; i < classes.length; i++){
                            classes[i] = Class.forName("com.tinslam.battleheart.entities.units.NPCs.enemyNPCs." + classNames[i]);
                        }
                        new SpawnCamp(classes, Integer.valueOf(typeTokens[2]), xTile * tileCellWidth, yTile * tileCellHeight, Float.valueOf(typeTokens[3]) * tileCellWidth, Float.valueOf(typeTokens[4]) * tileCellWidth);
                    }else if(type.startsWith("boss")){
                        String[] typeTokens = type.split("/");
                        Class<?> bossClass = Class.forName("com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses." + typeTokens[1]);
                        String[] coordsString = typeTokens[2].split("-");
                        try {
                            bossClass.getConstructor(float.class, float.class, Rect.class).newInstance(xTile * tileCellWidth, yTile * tileCellHeight,
                                    new Rect(Integer.parseInt(coordsString[0]) * tileCellWidth,
                                            Integer.parseInt(coordsString[1]) * tileCellHeight,
                                            Integer.parseInt(coordsString[2]) * tileCellWidth,
                                            Integer.parseInt(coordsString[3]) * tileCellHeight));
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }else if(type.startsWith("quest")){
                        String[] typeTokens = type.split("/");
                        Class<?> questClass = Class.forName("com.tinslam.battleheart.quests." + typeTokens[1]);
                        int id = Integer.parseInt(typeTokens[2]);
                        ArrayList<Integer> conditions = new ArrayList<>();
                        String[] conditionsStrings = typeTokens[3].split(",");
                        for(String conditionsString : conditionsStrings){
                            int tempId = Integer.parseInt(conditionsString);
                            if(tempId == 0) continue;
                            conditions.add(tempId);
                        }
                        try {
                            Quest questObject = (Quest) questClass.getConstructor(int.class, ArrayList.class).newInstance(id, conditions);
                            new QuestNpc(xTile * tileCellWidth, yTile * tileCellHeight).setQuest(questObject);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }else if(type.startsWith("collectable")){
                        String[] typeTokens = type.split("/");
                        Class<?> collectableClass = Class.forName("com.tinslam.battleheart.entities.collectables." + typeTokens[1]);
                        try {
                            collectableClass.getConstructor(float.class, float.class).newInstance(xTile * tileCellWidth, yTile * tileCellHeight);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }

                    switch(type){
                        case "ally_spawn_point" :
                            allySpawnPoints.add(new float[] {x, y});
                            break;
                    }
                }
                try{
                    line = br.readLine();
                }catch(Exception e){
                    line = "";
                    e.printStackTrace();
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        pathFindingMap.updateClearance();
        getMapRect().set(0, -getMapTop(), mapWidth * tileCellWidth, mapHeight * tileCellHeight);
        new Event() {
            @Override
            public void performAction() {
                SpawnCamp.spawnCamps();
            }
        };
    }

    public void bossDone(){
        createReplayFile();
        lockCamera = false;
        bossFightStarted = false;
        setFollowingDisabled(lastFollowState);
        if(getSelectedPc() != null){ // This will enable following.
            setSelectedPc(getSelectedPc());
        }
    }

    private void createReplayFile() {
        new Event() {
            @Override
            public void performAction() {
                replayFile.add("over");
                try{
                    PrintWriter writer = new PrintWriter(GameView.Context().openFileOutput("replay.txt", Context.MODE_PRIVATE));
                    for(String str : replayFile){
                        writer.println(str);
                    }
                    replayFile.clear();
                    writer.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        };
    }

    public void triggerBoss(Boss boss){
        if(showQuest){
            questToggleButton.callOnClick();
        }
        cutScene = true;
        cutSceneAlpha = 0;
        cutSceneCounter = 0;
        lockCamera = true;
        cameraLockArea = boss.getBossArea();
        cameraX = -boss.getBossArea().left;
        cameraY = -boss.getBossArea().top;
        cameraRect.set((int) -cameraX, (int) -cameraY, (int) -cameraX + cameraRect.width(), (int) -cameraY + cameraRect.height());
        lastFollowState = isFollowingDisabled();
        setFollowingDisabled(true);
        for(Pc pc : Pc.getPcs()){
            pc.resetCommands();
            if(pc == getSelectedPc()){
                continue;
            }
            pc.setFollow(false, null, 0);
            int x, y;
            int spawnRadius = GameView.getScreenHeight() / 2;
            do{
                x = Utils.getRandomIntegerInTheRange((int) (getSelectedPc().getX() - spawnRadius), (int) (getSelectedPc().getX() + spawnRadius), null);
                y = Utils.getRandomIntegerInTheRange((int) (getSelectedPc().getY() - spawnRadius), (int) (getSelectedPc().getY() + spawnRadius), null);
            }while(!DungeonState.getDungeonState().canMove(new Rect(x, y + pc.getCollisionBox().height() - pc.getSolidBox().height(), x + pc.getSolidBox().width(), y + pc.getCollisionBox().height()), pc.getXClearance(), pc.getYClearance()));
            pc.commandMove(x, y);
            pc.teleport(x, y);
        }
    }

    public void startReplay(Boss boss){
        replayFile.clear();
        replayFramesPassed = 0;
        replayStateId = 0;
        replayFile.add("state 0");
        replayMap.clear();
        for(Pc pc : Pc.getPcs()){
            String name = pc.getClass().getSimpleName().toLowerCase() + (int) (Math.random() * Integer.MAX_VALUE);
            replayFile.add("spawn_unit PCs." +
                    pc.getClass().getSimpleName() + " " +
                    name + " " +
                    pc.getX() + " " + pc.getY() + " " +
                    pc.getHp() + "/" + pc.getMaxHp());
            replayMap.put(pc, name);
        }
        String string = "spawn_spells " + getSpells().size();
        for(Spell x : getSpells()){
            String name = "spell" + (int) (Math.random() * Integer.MAX_VALUE);
            string = string.concat(" " + x.getClass().getSimpleName() + " " +
                    name + " " +
                    replayMap.get(x.getCaster()));
            replayMap.put(x, name);
        }
        replayFile.add(string);
        String name = boss.getClass().getSimpleName().toLowerCase() + (int) (Math.random() * Integer.MAX_VALUE);
        replayFile.add("spawn_boss " +
                boss.getClass().getSimpleName() + " " +
                name + " " +
                boss.getX() + " " + boss.getY() + " " +
                boss.getHp() + "/" + boss.getMaxHp() + " " +
                boss.getBossArea().left + "-" + boss.getBossArea().top + "-" +
                boss.getBossArea().right + "-" + boss.getBossArea().bottom + " " +
                boss.getSeed());
        replayMap.put(boss, name);
        bossFightStarted = true;
    }

    public void replayDamage(Unit unit, float damage, Unit attacker){
        if(bossFightStarted){
            String unitString = replayMap.get(unit);
            String attackerString = replayMap.get(attacker);
            if(unitString == null) return;
            newReplayState();
            replayFile.add("call_method " + unitString + " damageReplay " + damage + " " + attackerString);
        }
    }

    private void newReplayState(){
        if(replayFramesPassed != 0){
            replayFile.add("transition " + replayFramesPassed);
            replayFile.add("over");
            replayFile.add(" ");
            replayFile.add("state " + ++replayStateId);
            replayFramesPassed = 0;
        }
    }

    private void disableFollowing(){
        for(Pc pc : Pc.getPcs()){
            if(pc == getSelectedPc()) continue;
            pc.setFollow(false, null, 0);
        }
    }

    private void loadPcs(){
        if(TavernState.getCharactersPicked().isEmpty()){
            for(int i = 0; i < Utils.min(PlayerStats.getUnlockedCharacters().size(), 4); i++){
                TavernState.getCharactersPicked().add(PlayerStats.getUnlockedCharacters().get(i));
            }
        }
        for(int i = 0; i < TavernState.getCharactersPicked().size(); i++){
            switch(TavernState.getCharactersPicked().get(i)){
                case NameConsts.KNIGHT :
                    new Knight(allySpawnPoints.get(i)[0], allySpawnPoints.get(i)[1]);
                    break;

                case NameConsts.ARCHER :
                    new Archer(allySpawnPoints.get(i)[0], allySpawnPoints.get(i)[1]);
                    break;

                case NameConsts.PRIEST :
                    new Priest(allySpawnPoints.get(i)[0], allySpawnPoints.get(i)[1]);
                    break;

                default :
                    System.out.println("Loading a pc in dungeon with a name not known.");
                    System.exit(1);
            }
        }
        new Event() {
            @Override
            public void performAction() {
                new Event() {
                    @Override
                    public void performAction() {
                        loadSpells();
                        setSelectedPc(Pc.getPcs().get(0));
                        cameraX = -getSelectedPc().getX() - getSelectedPc().getCollisionBox().width() / 2 + GameView.getScreenWidth() / 2;
                        cameraY = -getSelectedPc().getY() - getSelectedPc().getCollisionBox().height() / 2 + GameView.getScreenHeight() / 2;
                        cameraRect.set((int) -cameraX, (int) -cameraY, (int) -cameraX + cameraRect.width(), (int) -cameraY + cameraRect.height());

                        for(final Pc x : Pc.getPcs()){
                            positionIndicators.add(new PositionOutsideCameraIndicator(x));
                        }
                        initPcPortraits();
                    }
                };
            }
        };
    }

    @Override
    public void battleStateWin() {

    }

    @Override
    public void battleStateLose() {
        loadPcs();
        bossDone();
        for(Boss boss : Boss.getBosses()){
            if(boss.isTriggered()){
                boss.reset();
            }
        }
    }

    @Override
    public void tickBattleState() {
        if(!vboCreated){
            vboCreated = true;
            createStaticVBOAndIBO();
        }
        if(!lockCamera){
            if(getSelectedPc() != null){
                cameraX = ((-getSelectedPc().getX() - getSelectedPc().getCollisionBox().width() / 2 + GameView.getScreenWidth() / 2) + cameraX) / 2;
                cameraY = ((-getSelectedPc().getY() - getSelectedPc().getCollisionBox().height() / 2 + GameView.getScreenHeight() / 2) + cameraY) / 2;
            }
            cameraRect.set((int) -cameraX, (int) -cameraY, (int) -cameraX + cameraRect.width(), (int) -cameraY + cameraRect.height());
        }else if(getSelectedPc() != null){
            float x = (-getSelectedPc().getX() - getSelectedPc().getCollisionBox().width() / 2 + GameView.getScreenWidth() / 2);
            float y = (-getSelectedPc().getY() - getSelectedPc().getCollisionBox().height() / 2 + GameView.getScreenHeight() / 2);
            cameraRect.left = Utils.min(cameraLockArea.right - GameView.getScreenWidth(), Utils.max((int) -x, cameraLockArea.left));
            cameraRect.top = Utils.min(cameraLockArea.bottom - GameView.getScreenHeight(), Utils.max((int) -y, cameraLockArea.top));
            cameraRect.right = cameraRect.left + GameView.getScreenWidth();
            cameraRect.bottom = cameraRect.top + GameView.getScreenHeight();
            cameraX = -cameraRect.left;
            cameraY = -cameraRect.top;
        }
        if(shakeScreen){
            shakeCounter++;
            if(shakeCounter % 2 == 1){
                cameraX += Utils.getRandomIntegerInTheRange((int) (-4 * GameView.density()), (int) (4 * GameView.density()), null);
                cameraY += Utils.getRandomIntegerInTheRange((int) (-4 * GameView.density()), (int) (4 * GameView.density()), null);
            }
            if(shakeCounter == shakeFrames){
                shakeScreen = false;
            }
        }
    }

    @Override
    public void renderBackground() {
        int w;
        int h;
        w = GameView.getScreenWidth() / 2;
        h = GameView.getScreenHeight() / 2;
        if(iboId1[0] != 0 && vboId1[0] != 0) MyGL2dRenderer.drawLabel(cameraX + GameView.getScreenWidth() / 2, cameraY + GameView.getScreenHeight(), w, -h, TextureData.atlas_0, 255,
                vboId1[0], iboId1[0], mapWidth * mapHeight * 6, 0);
        if(iboId2[0] != 0 && vboId2[0] != 0) MyGL2dRenderer.drawLabel(cameraX + GameView.getScreenWidth() / 2, cameraY + GameView.getScreenHeight(), w, -h, TextureData.atlas_0, 255,
                vboId2[0], iboId2[0], mapWidth * mapHeight * 6, 0);
    }

    @Override
    public void tickState(){
//        if(tickSlowMo()) return;
        Animation.tick();
        tick();
        for(Entity x : Entity.getEntities()){
//                if(Utils.distance(cameraRect.centerX(), cameraRect.centerY(), x.getX(), x.getY()) < cameraRect.width() || x instanceof Pc || x instanceof Projectile || x instanceof Boss){
                x.tick();
//                }
        }
        VisualEffect.tick();
        if(bossFightStarted){
            replayFramesPassed++;
        }
    }

    @Override
    public void renderState(){
        renderBackground();
        TouchHandler.render(cameraX, cameraY);
        if(getSelectedPc() != null){
            MyGL2dRenderer.drawLabel(getSelectedPc().getCollisionBox().left - 4 * GameView.density() + cameraX,
                    getSelectedPc().getCollisionBox().bottom - getSelectedPc().getCollisionBox().width() / 5 + cameraY,
                    getSelectedPc().getCollisionBox().width() + 8 * GameView.density(),
                    getSelectedPc().getCollisionBox().width() * 2 / 5,
                    TextureData.selected_character_visual, 255);
        }
        ArrayList<Entity> entitiesToBeDrawn = new ArrayList<>();
        ArrayList<HealthBarRenderer> healthBars = new ArrayList<>();
        for(Entity x : Entity.getEntities()){
            if(cameraRect.intersects(x.getCollisionBox().left, x.getCollisionBox().top, x.getCollisionBox().right, x.getCollisionBox().bottom) ||
                    (x instanceof Boss && ((Boss) x).isTriggered()) || x instanceof Projectile){
                entitiesToBeDrawn.add(x);
                if(x instanceof Unit){
                    HealthBarRenderer healthBarRenderer = ((Unit) x).getHealthBar();
                    if(healthBarRenderer != null && ((Unit) x).getShowHealthBar()) healthBars.add(healthBarRenderer);
                }
            }
        }
        sortEntities(entitiesToBeDrawn);
        for(Entity x : entitiesToBeDrawn){
            x.render(cameraX, cameraY);
        }
        for(HealthBarRenderer hb : healthBars){
            hb.renderHealthBar(cameraX, cameraY);
        }
        for(final PositionOutsideCameraIndicator indicator : positionIndicators){
            Unit x = indicator.getUnit();
            if(x == null || !x.doesExist()){
                new Event() {
                    @Override
                    public void performAction() {
                        positionIndicators.remove(indicator);
                    }
                };
                continue;
            }
            if(getSelectedPc() != x){
                if(!cameraRect.intersects(x.getCollisionBox().left, x.getCollisionBox().top, x.getCollisionBox().right, x.getCollisionBox().bottom)){
                    indicator.render(-cameraX + GameView.getScreenWidth() / 2, -cameraY + GameView.getScreenHeight() / 2);
                }
            }
        }
        Button.renderButtons(getButtons(), getButtonsLock());
        renderSpells();
        for(VisualEffect x : VisualEffect.getVisualEffects()){
            Animation animation = x.getAnimation();
            if(cameraRect.intersects((int) x.getX(), (int) x.getY(), (int) x.getX() + animation.getWidth(), (int) (x.getY() + animation.getHeight()))){
                x.renderVisualEffect(cameraX, cameraY);
            }
        }
        if(!lockCamera && followButton != null) followButton.render();
        if(fpsTextRenderer != null) fpsTextRenderer.setText("Average FPS : " + GameThread.avgFps);
        renderOver();
        if(confirmation){
            drawConfirmation();
        }
//        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.solid_dark_blue, 92);
//        weatherAnimation.render(0, 0);
        drawLoading();
        renderCutScene();
    }

    private void renderCutScene(){
        if(cutScene){
            if(cutSceneCounter < 255 / 12){
                cutSceneAlpha += 12;
            }else if(cutSceneCounter >= 600 / 12){
                cutSceneAlpha -= 12;
                if(cutSceneAlpha == 0) cutScene = false;
            }
            cutSceneCounter++;
            MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight() / 6, TextureData.solid_black, cutSceneAlpha);
            MyGL2dRenderer.drawLabel(0, GameView.getScreenHeight() * 5 / 6, GameView.getScreenWidth(), GameView.getScreenHeight() / 6, TextureData.solid_black, cutSceneAlpha);
        }
    }

    public boolean cutSceneEnded(){
        return !cutScene && cutSceneCounter >= 600 / 12;
    }

    @Override
    public void renderBattleState() {

    }

    @Override
    public void renderOverBattleState() {

    }

    /**
     * Checks whether an area on the map is possible to move to.
     */
    public boolean canMove(Rect src, int xClearance, int yClearance) {
//        if(lockCamera){
//            if(!cameraRect.contains(src.left, src.top, src.right, src.bottom)) return false;
//        }
        Rect rect = new Rect(src.left, src.top, src.left + src.width() / xClearance, src.top + src.height() / yClearance);
        try{
            ArrayList<int[]> tilesIntersected = getTilesIntersected(rect);
            for(int i = 0; i < tilesIntersected.size(); i++){
                Node node = pathFindingMap.get(tilesIntersected.get(i)[0], tilesIntersected.get(i)[1]);
                if(node == null || node.xClearance < xClearance || node.yClearance < yClearance){
                    return false;
                }
            }
        }catch(Exception e){
            return false;
        }

        return true;
    }

    private ArrayList<int[]> getTilesIntersected(Rect rect){
        ArrayList<int[]> list = new ArrayList<>();

        int x1 = rect.left / tileCellWidth;
        int y1 = rect.top / tileCellHeight;
        int x2 = rect.right / tileCellWidth;
        int y2 = rect.bottom / tileCellHeight;



        for(int i = x1; i <= x2; i++){
            for(int j = y1; j <= y2; j++){
                list.add(new int[] {i, j});
            }
        }

        return list;
    }

    public boolean onUp(int x, int y){
        if(showingMessage){
            if(!Utils.isInRect(x + cameraX, y + cameraY, GameView.getScreenWidth() / 10, GameView.getScreenHeight() / 12, GameView.getScreenWidth() * 9 / 10, GameView.getScreenHeight() / 6 / 4)){
                messagePanel.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                showingMessage = false;
            }
            return true;
        }
        if(showQuest) if(Utils.isInRect(x + cameraX, y + cameraY, questsPanelXOffset, questsPanelYOffset, questsPanelXOffset + questPanelWidth, questsPanelYOffset + questPanelHeight + (currentQuest == null ? 0 : questProgressHeight))) return true;
        for(QuestNpc npc : QuestNpc.getQuestNpcs()){
            if(Utils.isInRect(x, y, npc.getCollisionBox())){
                if(getSelectedPc() != null && Utils.distance(getSelectedPc().getCollisionBox().centerX(), getSelectedPc().getCollisionBox().centerY()
                        , npc.getCollisionBox().centerX(), npc.getCollisionBox().centerY()) < 128 * GameView.density()){
                    npc.interact();
                    return true;
                }
            }
        }
        for(Collectable collectable : Collectable.getCollectables()){
            if(Utils.isInRect(x, y, collectable.getCollisionBox())){
                if(getSelectedPc() != null && Utils.distance(getSelectedPc().getCollisionBox().centerX(), getSelectedPc().getCollisionBox().centerY()
                        , collectable.getCollisionBox().centerX(), collectable.getCollisionBox().centerY()) < 128 * GameView.density()){
                    collectable.interact();
                    return true;
                }
            }
        }

        return Sign.onUp(x, y);
    }

    /**
     * Checks whether an area on the map is out of bounds.
     */
    @Override
    public boolean isOutOfBounds(int x, int y) {
        return false;
    }

    /**
     * This method is called when the game view loses focus.
     */
    @Override
    public void surfaceDestroyed() {

    }

    /**
     * Listener for handling the key events.
     *
     * @param event The key event.
     */
    @Override
    public void handleKeyEvent(KeyEvent event) {

    }

    public void showMessage(String text) {
        showingMessage = true;
        message.setText(text);
        message.measure(0, 0);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
        params.leftMargin = GameView.getScreenWidth() / 10;
        params.topMargin = GameView.getScreenHeight() / 12 + GameView.getScreenHeight() / 12 - message.getMeasuredHeight() / 2;
        params.width = GameView.getScreenWidth() * 8 / 10;
        params.height = GameView.getScreenHeight() / 6;
        message.setLayoutParams(params);
        messagePanel.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
    }

    private void createStaticVBOAndIBO(){
        try{
            GLES20.glDeleteBuffers(1, vboId1, 0);
            GLES20.glDeleteBuffers(1, vboId2, 0);
            GLES20.glDeleteBuffers(1, iboId1, 0);
            GLES20.glDeleteBuffers(1, iboId2, 0);
            createLayer(0, vboId1, iboId1);
            createLayer(1, vboId2, iboId2);
        }catch(Exception e){
            vboCreated = false;
        }
    }

    private void createLayer(int layer, int[] vboId, int[] iboId){
        float[] vertices = new float[mapWidth * mapHeight * 4 * 5];
        short[] indices = new short[mapWidth * mapHeight * 6];
        for(int i = 0; i < mapWidth; i++){
            for(int j = 0; j < mapHeight; j++){
                if(tiles[i][j].get(layer) == -1) continue;
                indices[6 * (i * mapHeight + j)] = (short) (4 * (i * mapHeight + j));
                indices[6 * (i * mapHeight + j) + 1] = (short) (4 * (i * mapHeight + j) + 2);
                indices[6 * (i * mapHeight + j) + 2] = (short) (4 * (i * mapHeight + j) + 1);
                indices[6 * (i * mapHeight + j) + 3] = (short) (4 * (i * mapHeight + j) + 1);
                indices[6 * (i * mapHeight + j) + 4] = (short) (4 * (i * mapHeight + j) + 2);
                indices[6 * (i * mapHeight + j) + 5] = (short) (4 * (i * mapHeight + j) + 3);

                vertices[5 * 4 * (i * mapHeight + j)] = MyGL2dRenderer.toOpenGLCoordsX(i * tileCellWidth);
                vertices[5 * 4 * (i * mapHeight + j) + 1] = MyGL2dRenderer.toOpenGLCoordsY(j * tileCellHeight);
                vertices[5 * 4 * (i * mapHeight + j) + 2] = 0;
                vertices[5 * 4 * (i * mapHeight + j) + 3] = getZeroXTextureCoord(tiles[i][j].get(layer));
                vertices[5 * 4 * (i * mapHeight + j) + 4] = getOneYTextureCoord(tiles[i][j].get(layer));

                vertices[5 * 4 * (i * mapHeight + j) + 5] = MyGL2dRenderer.toOpenGLCoordsX(i * tileCellWidth);
                vertices[5 * 4 * (i * mapHeight + j) + 6] = MyGL2dRenderer.toOpenGLCoordsY(j * tileCellHeight + tileCellHeight);
                vertices[5 * 4 * (i * mapHeight + j) + 7] = 0;
                vertices[5 * 4 * (i * mapHeight + j) + 8] = getZeroXTextureCoord(tiles[i][j].get(layer));
                vertices[5 * 4 * (i * mapHeight + j) + 9] = getZeroYTextureCoord(tiles[i][j].get(layer));

                vertices[5 * 4 * (i * mapHeight + j) + 10] = MyGL2dRenderer.toOpenGLCoordsX(i * tileCellWidth + tileCellWidth);
                vertices[5 * 4 * (i * mapHeight + j) + 11] = MyGL2dRenderer.toOpenGLCoordsY(j * tileCellHeight);
                vertices[5 * 4 * (i * mapHeight + j) + 12] = 0;
                vertices[5 * 4 * (i * mapHeight + j) + 13] = getOneXTextureCoord(tiles[i][j].get(layer));
                vertices[5 * 4 * (i * mapHeight + j) + 14] = getOneYTextureCoord(tiles[i][j].get(layer));

                vertices[5 * 4 * (i * mapHeight + j) + 15] = MyGL2dRenderer.toOpenGLCoordsX(i * tileCellWidth + tileCellWidth);
                vertices[5 * 4 * (i * mapHeight + j) + 16] = MyGL2dRenderer.toOpenGLCoordsY(j * tileCellHeight + tileCellHeight);
                vertices[5 * 4 * (i * mapHeight + j) + 17] = 0;
                vertices[5 * 4 * (i * mapHeight + j) + 18] = getOneXTextureCoord(tiles[i][j].get(layer));
                vertices[5 * 4 * (i * mapHeight + j) + 19] = getZeroYTextureCoord(tiles[i][j].get(layer));
            }
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * MyGLRenderer.BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vbo = bb.asFloatBuffer();
        vbo.put(vertices);
        vbo.position(0);

        GLES20.glGenBuffers(1, vboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vbo.capacity() * MyGLRenderer.BYTES_PER_FLOAT,
                vbo, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * MyGLRenderer.BYTES_PER_SHORT);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer ibo = dlb.asShortBuffer();
        ibo.put(indices);
        ibo.position(0);

        GLES20.glGenBuffers(1, iboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboId[0]);

        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo.capacity() * MyGLRenderer.BYTES_PER_SHORT,
                ibo, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private float getZeroXTextureCoord(int data){
        float xOffset;
        float maxWidth = 1024;
        if(data < tiles2Start){
            xOffset = 0;
        }else if(data < tiles3Start){
            xOffset = 768;
            data -= tiles2Start;
        }else if(data < tiles4Start){
            xOffset = 256;
            data -= tiles3Start;
        }else if(data < tiles5Start){
            xOffset = 256;
            data -= tiles4Start;
        }else if(data < tiles6Start){
            xOffset = 512;
            data -= tiles5Start;
        }else if(data < tiles7Start){
            xOffset = 0;
            data -= tiles6Start;
        }else{
            xOffset = 512;
            data -= tiles7Start;
        }
        data %= 8;
        data *= 32;
        return (xOffset + data + 0.5f) / maxWidth;
    }

    private float getOneXTextureCoord(int data){
        float xOffset;
        float maxWidth = 1024;
        if(data < tiles2Start){
            xOffset = 0;
        }else if(data < tiles3Start){
            xOffset = 768;
            data -= tiles2Start;
        }else if(data < tiles4Start){
            xOffset = 256;
            data -= tiles3Start;
        }else if(data < tiles5Start){
            xOffset = 256;
            data -= tiles4Start;
        }else if(data < tiles6Start){
            xOffset = 512;
            data -= tiles5Start;
        }else if(data < tiles7Start){
            xOffset = 0;
            data -= tiles6Start;
        }else{
            xOffset = 512;
            data -= tiles7Start;
        }
        data %= 8;
        data++;
        data *= 32;
        return (xOffset + data - 0.5f) / maxWidth;
    }

    private float getOneYTextureCoord(int data){
        float yOffset;
        float maxHeight = 1856;
        if(data < tiles2Start){
            yOffset = 0;
        }else if(data < tiles3Start){
            yOffset = 0;
            data -= tiles2Start;
        }else if(data < tiles4Start){
            yOffset = 800;
            data -= tiles3Start;
        }else if(data < tiles5Start){
            yOffset = 0;
            data -= tiles4Start;
        }else if(data < tiles6Start){
            yOffset = 0;
            data -= tiles5Start;
        }else if(data < tiles7Start){
            yOffset = 800;
            data -= tiles6Start;
        }else{
            yOffset = 800;
            data -= tiles7Start;
        }
        data /= 8;
        data *= 32;
        return (yOffset + data + 0.5f) / maxHeight;
    }

    private float getZeroYTextureCoord(int data){
        float yOffset;
        float maxHeight = 1856;
        if(data < tiles2Start){
            yOffset = 0;
        }else if(data < tiles3Start){
            yOffset = 0;
            data -= tiles2Start;
        }else if(data < tiles4Start){
            yOffset = 800;
            data -= tiles3Start;
        }else if(data < tiles5Start){
            yOffset = 0;
            data -= tiles4Start;
        }else if(data < tiles6Start){
            yOffset = 0;
            data -= tiles5Start;
        }else if(data < tiles7Start){
            yOffset = 800;
            data -= tiles6Start;
        }else{
            yOffset = 800;
            data -= tiles7Start;
        }
        data /= 8;
        data++;
        data *= 32;
        return (yOffset + data - 0.5f) / maxHeight;
    }

    private void shakeScreen(int frameCount){
        shakeFrames = frameCount;
        shakeScreen = true;
        shakeCounter = 0;
    }

    @Override
    public void end(){
        super.end();
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message.setVisibility(View.GONE);
                messagePanel.setVisibility(View.GONE);
                questProgress.setVisibility(View.GONE);
                questProgressPanel.setVisibility(View.GONE);
                questName.setVisibility(View.GONE);
                questNamePanel.setVisibility(View.GONE);
                questShowAllButton.setVisibility(View.GONE);
                questToggleButton.setVisibility(View.GONE);
                questPanel.setVisibility(View.GONE);
                questDescription.setVisibility(View.GONE);
                questReward.setVisibility(View.GONE);
                for(TextView tv : questRewards){
                    tv.setVisibility(View.GONE);
                }
                for(ImageView button : questActiveButtons){
                    questActivesTexts.get(button).setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }
                questButtonActiveTracker.clear();
                questActivesTexts.clear();
            }
        });
    }

    public void shakeScreen(){
        shakeScreen(Integer.MAX_VALUE);
    }

    public void stopScreenShaking(){
        shakeScreen = false;
    }

    public static DungeonState getDungeonState(){
        return (DungeonState) BattleState.getBattleState();
    }

    private void setFollowingDisabled(boolean flag){
        followingDisabled = flag;
        if(flag) disableFollowing();
        else if(getSelectedPc() != null) setSelectedPc(getSelectedPc());
    }

    boolean isFollowingDisabled() {
        return followingDisabled;
    }

    public Quest getCurrentQuest() {
        return currentQuest;
    }

    public void replayCommandMove(Unit unit, float x, float y) {
        if(!bossFightStarted) return;
        newReplayState();
        replayFile.add("command " + replayMap.get(unit) + " move " + x + " " + y);
    }

    public void replayCommandAttack(Unit attacker, Unit target){
        if(!bossFightStarted) return;
        newReplayState();
        replayFile.add("command " + replayMap.get(attacker) + " attack " + replayMap.get(target));
    }

    public void replaySelectedPc(Pc selectedPc) {
        if(!bossFightStarted) return;
        newReplayState();
        replayFile.add("command setSelectedPc " + replayMap.get(selectedPc));
    }
}
