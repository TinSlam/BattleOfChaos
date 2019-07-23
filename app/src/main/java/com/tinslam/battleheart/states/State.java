package com.tinslam.battleheart.states;

import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tinslam.battleheart.R;
import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.HealthBarRenderer;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.UI.graphics.visualEffects.VisualEffect;
import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.base.GameThread;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.entities.Entity;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.gameUtility.SpawnCamp;
import com.tinslam.battleheart.gameUtility.SpellTouchInput;
import com.tinslam.battleheart.gameUtility.TouchHandler;
import com.tinslam.battleheart.quests.Quest;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * The class that controls the current state of the game.
 */
public abstract class State{
    private ArrayList<Button> buttons = new ArrayList<>();
    private final Object buttonsLock = new Object();
    private static int slowMoCounter = 0;
    public static boolean slowMo = false;
    SpellTouchInput touchInput;
    private String lastState = DashboardState.class.getSimpleName();
    protected boolean confirmation = false;
    protected TextRenderer fpsTextRenderer;
    private boolean loadingEnded;
    private static int loadingAlpha = 255;

    private Button confirmButton;
    private static int confirmWidth = GameView.getScreenWidth() / 2;
    private static int confirmHeight = GameView.getScreenHeight() / 3;
    private static int confirmXOffset = (GameView.getScreenWidth() - confirmWidth) / 2;
    private static int confirmYOffset = (GameView.getScreenHeight() - confirmHeight) / 2;

    private TextView textView, textViewYes, textViewNo;
    private ImageView imageView, imageViewYes, imageViewNo;


    /**
     * Constructor.
     */
    State(){

    }

    public void initConfirmationPanel(){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int buttonWidth = confirmWidth / 4;
                int buttonHeight = confirmHeight / 3;
                imageView = new ImageView(OpenGL2dActivity.openGL2dActivity);
                imageViewYes = new ImageView(OpenGL2dActivity.openGL2dActivity);
                imageViewNo = new ImageView(OpenGL2dActivity.openGL2dActivity);
                textView = new TextView(OpenGL2dActivity.openGL2dActivity);
                textViewYes = new TextView(OpenGL2dActivity.openGL2dActivity);
                textViewNo = new TextView(OpenGL2dActivity.openGL2dActivity);
                OpenGL2dActivity.openGL2dActivity.addContentView(imageView, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(imageViewYes, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(imageViewNo, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(textView, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(textViewYes, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                OpenGL2dActivity.openGL2dActivity.addContentView(textViewNo, new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTranslationZ(100);
                    imageViewNo.setTranslationZ(100);
                    imageViewYes.setTranslationZ(100);
                    textView.setTranslationZ(100);
                    textViewYes.setTranslationZ(100);
                    textViewNo.setTranslationZ(100);
                }
                imageView.setImageBitmap(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.confirmation_panel), confirmWidth, confirmHeight));
                imageViewYes.setImageBitmap(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.button_empty), buttonWidth, buttonHeight));
                imageViewNo.setImageBitmap(Image.resizeImage(BitmapFactory.decodeResource(GameView.Context().getResources(), R.drawable.button_empty), buttonWidth, buttonHeight));
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = confirmXOffset;
                params.topMargin = confirmYOffset;
                params.width = confirmWidth;
                params.height = confirmHeight;
                imageView.setLayoutParams(params);
                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = confirmXOffset + confirmXOffset / 8;
                params.topMargin = confirmYOffset + confirmHeight / 2 + (confirmHeight / 2 - buttonHeight) / 2;
                params.width = buttonWidth;
                params.height = buttonHeight;
                imageViewNo.setLayoutParams(params);
                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = confirmXOffset + confirmWidth - confirmXOffset / 8 - buttonWidth;
                params.topMargin = confirmYOffset + confirmHeight / 2 + (confirmHeight / 2 - buttonHeight) / 2;
                params.width = buttonWidth;
                params.height = buttonHeight;
                imageViewYes.setLayoutParams(params);
                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                textView.setText("");
                textViewYes.setText(GameView.string(R.string.yes));
                textViewNo.setText(GameView.string(R.string.no));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textViewYes.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textViewNo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                params.leftMargin = confirmXOffset + confirmWidth / 20;
                params.topMargin = confirmYOffset + confirmHeight / 5;
                params.width = confirmWidth * 9 / 10;
                params.height = confirmHeight / 4;
                textView.setLayoutParams(params);
                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                textViewYes.measure(0, 0);
                int textHeight = textViewYes.getMeasuredHeight();
                params.leftMargin = confirmXOffset + confirmWidth - confirmXOffset / 8 - buttonWidth + buttonWidth / 10;
                params.topMargin = (int) (confirmYOffset + confirmHeight / 2 + (confirmHeight / 2 - buttonHeight) / 2 + (buttonHeight - textHeight) / 2 - textViewYes.getPaint().descent());
                params.width = buttonWidth * 4 / 5;
                params.height = textHeight;
                textViewYes.setLayoutParams(params);
                params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                textViewNo.measure(0, 0);
                textHeight = textViewNo.getMeasuredHeight();
                params.leftMargin = confirmXOffset + confirmXOffset / 8 + buttonWidth / 10;
                params.topMargin = (int) (confirmYOffset + confirmHeight / 2 + (confirmHeight / 2 - buttonHeight) / 2 + (buttonHeight - textHeight) / 2 - textViewYes.getPaint().descent());
                params.width = buttonWidth * 4 / 5;
                params.height = textHeight;
                textViewNo.setLayoutParams(params);
                textView.setVisibility(View.GONE);
                textViewNo.setVisibility(View.GONE);
                textViewYes.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                imageViewNo.setVisibility(View.GONE);
                imageViewYes.setVisibility(View.GONE);

                imageViewYes.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        try {
                            Method method = confirmButton.getClass().getDeclaredMethod("performOnUp");
                            method.invoke(confirmButton);
                            setConfirmation(false, null, null);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });

                imageViewNo.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Method method;
                        try{
                            method = confirmButton.getClass().getDeclaredMethod("performOnNo");
                            method.invoke(confirmButton);
                        }catch(Exception ignored){}
                        setConfirmation(false, null, null);
                    }
                });
            }
        });
        fpsTextRenderer = new TextRenderer("Fps", 16 * GameView.density(), GameView.getScreenHeight() - 16 * GameView.density(),
                Float.MAX_VALUE, GameView.getScreenHeight() / 20, Paint.Align.LEFT, false, false);
        fpsTextRenderer.setTextSize(16 * GameView.density());
        fpsTextRenderer.show();
    }

    void loadingEnded(){
        loadingEnded = true;
        loadingAlpha = 255;
    }

    /**
     * This method is called when the game view loses focus.
     */
    public abstract void surfaceDestroyed();

    /**
     * Listener for handling the back key events.
     */
    public void handleBackPressed(){
        if(confirmation) return;
        try{
            if(GameView.stateChangeOnCD) return;
            Class stateClass = Class.forName("com.tinslam.battleheart.states." + lastState);
            State state = (State) stateClass.newInstance();
            GameView.setState(state, "");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }
    }

    /**
     * Listener for handling the key events.
     * @param event The key event.
     */
    public abstract void handleKeyEvent(KeyEvent event);

    /**
     * Determines the action of the touch event.
     * @param event Touch event.
     * @return Something.
     */
    private boolean breakDownEvent(final MotionEvent event){
        if(confirmation) return true;
        if(touchInput != null){
            if(!(this instanceof ReplayState)){
                new Event() {
                    @Override
                    public void performAction() {
                        if(touchInput != null) touchInput.handleInput(event);
                    }
                };
                return true;
            }
        }
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN :
                if(Button.onActionDown(event, getButtons(), getButtonsLock()) || onActionDown(event)) return true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN :
                if(Button.onActionPointerDown(event, getButtons(), getButtonsLock()) || onActionPointerDown(event)) return true;
                break;

            case MotionEvent.ACTION_MOVE :
                if(Button.onActionMove(event, getButtons(), getButtonsLock()) || onActionMove(event)) return true;
                break;

            case MotionEvent.ACTION_UP :
                if(Button.onActionUp(event, getButtons(), getButtonsLock()) || onActionUp(event)) return true;
                break;

            case MotionEvent.ACTION_POINTER_UP :
                if(Button.onActionPointerUp(event, getButtons(), getButtonsLock()) || onActionPointerUp(event)) return true;
                break;
        }

        return true;
    }

    /**
     * Initializes the state. Is called when switching to a new state.
     */
    public abstract void startState();

    /**
     * Ticks the state.
     */
    public abstract void tick();

    /**
     * Ticks the state.
     * Ticks the visual effects.
     * Ticks all the existing entities.
     */
    public void tickState(){
//        if(tickSlowMo()) return;
        Animation.tick();
        tick();
        for(Entity x : Entity.getEntities()){
            x.tick();
        }
        VisualEffect.tick();
    }

    /**
     * Renders the state.
     */
    public abstract void render();

    /**
     * Is drawn after everything.
     */
    public abstract void renderOver();

    /**
     * Renders the state.
     * Renders all the touch inputs.
     * Renders all the existing entities.
     * Renders all the visual effects.
     */
    public void renderState(){
        render();
        TouchHandler.render(0, 0);
        Button.renderButtons(buttons, buttonsLock);
        for(Entity x : Entity.getEntities()){
            x.render(0, 0);
        }
        VisualEffect.render(0, 0);
        HealthBarRenderer.render(0, 0);
        renderOver();
        if(confirmation){
            drawConfirmation();
        }
        if(fpsTextRenderer != null) fpsTextRenderer.setText("Average FPS : " + GameThread.avgFps);
        drawLoading();
    }

    protected void drawLoading(){
        if(loadingEnded){
            MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.solid_black, loadingAlpha);
            loadingAlpha = Utils.max(0, loadingAlpha - 10);
            if(loadingAlpha == 0) loadingEnded = false;
        }
    }

    /**
     * Draws the conformation panel.
     */
    protected void drawConfirmation(){
        MyGL2dRenderer.drawLabel(0, 0, GameView.getScreenWidth(), GameView.getScreenHeight(), TextureData.transparent_black_square, 255);
        MyGL2dRenderer.drawLabel(confirmXOffset, confirmYOffset, confirmWidth, confirmHeight, TextureData.confirmation_panel, 255);
    }

    /**
     * Handles touch inputs.
     * @param event The touch event.
     */
    public boolean onTouchEvent(MotionEvent event){
        return breakDownEvent(event);
    }

    /**
     * Handles touch input when action is pressing and is the first finger touching the screen.
     */
    public abstract boolean onActionDown(MotionEvent event);

    /**
     * Handles touch input when action is pressing and is not the first finger touching the screen.
     */
    public abstract boolean onActionPointerDown(MotionEvent event);

    /**
     * Handles touch input when action is moving finger on the screen.
     */
    public abstract boolean onActionMove(MotionEvent event);

    /**
     * Handles touch input when action is releasing and is the first finger touching the screen.
     */
    public abstract boolean onActionUp(MotionEvent event);

    /**
     * Handles touch input when action is releasing and is not the first finger touching the screen.
     */
    public abstract boolean onActionPointerUp(MotionEvent event);

    /**
     * Is called when state is to be changed.
     */
    public abstract void endState();

    /**
     * Is called when state is to be changed.
     * Clears the touch inputs.
     * Clears the visual effects.
     * Destroys all the entities.
     * Calls end state for the child.
     */
    public void end(){
        new Event() {
            @Override
            public void performAction() {
                TextRenderer.clear();
                Event.clear();
                Quest.clear();
                Animation.endAnimations();
                TouchHandler.clear();
                VisualEffect.clear();
                HealthBarRenderer.clear();
                SpellTouchInput.clear();
                SpawnCamp.clear();
                for(Entity x : Entity.getEntities()){
                    x.destroy();
                }
                endState();
            }
        };
    }

    public void setSlowMo(boolean flag){
        if(touchInput != null) return;
        if(flag){
            slowMoCounter = 0;
            slowMo = true;
        }else slowMo = false;
    }

    /**
     *
     * @return An ArrayList of all the active buttons on the state.
     */
    public ArrayList<Button> getButtons() {
        return buttons;
    }

    /**
     *
     * @return A lock that controls the synchronization of the actions performed on the buttons list.
     */
    public Object getButtonsLock() {
        return buttonsLock;
    }

    boolean getSlowMo() {
        return slowMo;
    }

    public SpellTouchInput getTouchInput() {
        return touchInput;
    }

    public void setTouchInput(SpellTouchInput touchInput) {
        if(touchInput == null){
            setSlowMo(false);
        }else{
            setSlowMo(true);
        }
        this.touchInput = touchInput;
    }

    public void setLastState(String lastState){
        this.lastState = lastState;
    }

    public void setConfirmation(final boolean flag, Button button, final String text){
        if(imageView == null) return;
        confirmButton = button;
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(flag){
                    imageView.setVisibility(View.VISIBLE);
                    imageViewNo.setVisibility(View.VISIBLE);
                    imageViewYes.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    textViewYes.setVisibility(View.VISIBLE);
                    textViewNo.setVisibility(View.VISIBLE);
                }else{
                    imageView.setVisibility(View.GONE);
                    imageViewNo.setVisibility(View.GONE);
                    imageViewYes.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    textViewYes.setVisibility(View.GONE);
                    textViewNo.setVisibility(View.GONE);
                }
                confirmation = flag;
                if(flag){
                    if(text == null){
                        textView.setText(GameView.string(R.string.are_you_sure_you_want_to_continue));
                    }else{
                        textView.setText(text);
                    }
                }
            }
        });
    }
}