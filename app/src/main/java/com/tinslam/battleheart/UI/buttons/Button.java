package com.tinslam.battleheart.UI.buttons;

import android.graphics.Paint;
import android.view.MotionEvent;

import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.gameUtility.Event;
import com.tinslam.battleheart.interfaces.ButtonInterface;

import java.util.ArrayList;

/**
 * The class that handles all the buttons.
 */
public abstract class Button implements ButtonInterface{
    protected Texture image, imageOnClick, currentImage;
    private boolean active, manualRender;
    private int pointerId = -1;
    protected Paint paint = new Paint();
    protected TextRenderer textRenderer;
    protected boolean flipped = false;
//    private boolean shrink = false;

    private static final Object buttonsLock = new Object();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static ArrayList<Button> buttons = new ArrayList<>();

    /**
     * Constructor.
     * @param image The image of the button.
     * @param imageOnClick The image of the button when clicked.
     * @param manualRender Whether the button should be rendered manually or is automatically rendered in the state class.
     */
    public Button(Texture image, Texture imageOnClick, boolean manualRender){
        this.image = image;
        this.imageOnClick = imageOnClick;
        this.manualRender = manualRender;
        currentImage = image;

        new Event() {
            @Override
            public void performAction() {
                buttons.add(Button.this);
                GameView.getState().getButtons().add(Button.this);
            }
        };
        setActive(true);
    }

    /**
     * Renders all the buttons. Skips the ones that are not supposed to be rendered automatically.
     */
    public static void renderButtons(ArrayList<Button> stateButtons, Object lock){
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        for (Button x : stateButtons) {
            if(!x.manualRender) x.render();
        }
    }

    /**
     * Resizes the button based on its center.
     */
    public abstract void resizeImage(int newWidth, int newHeight);

    /**
     * Translates the button.
     */
    public abstract void translate(int dx, @SuppressWarnings("SameParameterValue") int dy);

    /**
     * Renders the button.
     */
    public abstract void render();

    /**
     * Check the other method.
     */
    public static boolean onActionDown(MotionEvent event, Button button){
        if(!button.active) return false;

        int mx = (int) event.getX(event.getActionIndex());
        int my = (int) event.getY(event.getActionIndex());

        if (button.isTouched(mx, my)){
            if(button.onDownDefault()){
                button.pointerId = event.getPointerId(event.getActionIndex());
                return true;
            }
        }

        return false;
    }

    /**
     * Check the other method.
     */
    public static boolean onActionUp(MotionEvent event, Button button){
        if(!button.active) return false;

        int mx = (int) event.getX(event.getActionIndex());
        int my = (int) event.getY(event.getActionIndex());

        if(button.pointerId == event.getPointerId(event.getActionIndex())) {
            button.pointerId = -1;
            button.currentImage = button.image;
            if (button.isTouched(mx, my)) {
                button.onUp();
            }
            button.setCurrentImage(button.getImage());
        }

        return false;
    }

    /**
     * Handles touch input when action is pressing and is the first finger touching the screen.
     */
    public static boolean onActionDown(MotionEvent event, ArrayList<Button> stateButtons, Object lock){
        int mx = (int) event.getX(event.getActionIndex());
        int my = (int) event.getY(event.getActionIndex());

        for (Button x : stateButtons) {
            if(!x.active) continue;
            if (x.isTouched(mx, my)){
                if(x.onDownDefault()){
                    x.pointerId = event.getPointerId(event.getActionIndex());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Handles touch input when action is pressing and is not the first finger touching the screen.
     */
    public static boolean onActionPointerDown(MotionEvent event, ArrayList<Button> stateButtons, Object lock){
        int mx = (int) event.getX(event.getActionIndex());
        int my = (int) event.getY(event.getActionIndex());

        for (Button x : stateButtons) {
            if(!x.active) continue;
            if (x.isTouched(mx, my)) {
                if(x.onDownDefault()) {
                    x.pointerId = event.getPointerId(event.getActionIndex());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Handles touch input when action is moving finger on the screen.
     */
    public static boolean onActionMove(MotionEvent event, ArrayList<Button> stateButtons, Object lock){
        for (Button x : stateButtons) {
            if(!x.active) continue;
            if(x.pointerId != -1){
                int index = -1;
                for(int i = 0; i < 5; i++){
                    if(event.getPointerId(i) == x.pointerId){
                        index = i;
                        break;
                    }
                }
                int mx = (int) event.getX(index);
                int my = (int) event.getY(index);
                if(x.isTouched(mx, my)){
                    x.setCurrentImage(x.getImageOnClick());
                }else{
                    x.setCurrentImage(x.getImage());
                }
            }
        }

        return false;
    }

    /**
     * Handles touch input when action is releasing and is the first finger touching the screen.
     */
    public static boolean onActionUp(MotionEvent event, ArrayList<Button> stateButtons, Object lock){
        int mx = (int) event.getX(event.getActionIndex());
        int my = (int) event.getY(event.getActionIndex());

        for (Button x : stateButtons) {
            if(!x.active) continue;
            if(x.pointerId == event.getPointerId(event.getActionIndex())) {
                x.pointerId = -1;
                x.currentImage = x.image;
                if (x.isTouched(mx, my)) {
                    if(x.onUp()) {
                        return true;
                    }
                }
                x.setCurrentImage(x.getImage());
            }
        }

        return false;
    }

    /**
     * Handles touch input when action is releasing and is not the first finger touching the screen.
     */
    public static boolean onActionPointerUp(MotionEvent event, ArrayList<Button> stateButtons, Object lock){
        int mx = (int) event.getX(event.getActionIndex());
        int my = (int) event.getY(event.getActionIndex());

        for (Button x : stateButtons) {
            if(!x.active) continue;
            if(x.pointerId == event.getPointerId(event.getActionIndex())) {
                x.pointerId = -1;
                x.currentImage = x.image;
                if (x.isTouched(mx, my)) {
                    if(x.onUp()) {
                        return true;
                    }
                }
                x.setCurrentImage(x.getImage());
            }
        }

        return false;
    }

    /**
     * Is called when button is pressed.
     */
    private boolean onDownDefault(){
        setCurrentImage(imageOnClick);
        return onDown();
    }

    /**
     * Is called when button is pressed.
     */
    public abstract boolean onDown();

    /**
     * Is called when button is released.
     */
    public abstract boolean onUp();

    /**
     * Checks whether the button contains the point (x, y).
     */
    public abstract boolean isTouched(int x, int y);

    /**
     *
     * @return The active state of the button.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active state of the button.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *
     * @return The image of the button.
     */
    public Texture getImage() {
        return image;
    }

    /**
     * Sets the image of the button.
     */
    public void setImage(Texture image) {
        this.image = image;
        setCurrentImage(image);
    }

    /**
     *
     * @return The on press image of the button.
     */
    private Texture getImageOnClick() {
        return imageOnClick;
    }

    /**
     * Sets the on press image of the button.
     */
    protected void setImageOnClick(Texture imageOnClick) {
        this.imageOnClick = imageOnClick;
    }

    /**
     *
     * @return The current image of the button that is being drawn.
     */
    public Texture getCurrentImage() {
        return currentImage;
    }

    /**
     * Sets the current image of the button that is to be drawn.
     */
    private void setCurrentImage(Texture currentImage) {
        this.currentImage = currentImage;
    }

    /**
     * Repositions the button.
     */
    public abstract void setPosition(int x, int y);

    /**
     * @return The x position of the button.
     */
    public abstract int getX();

    /**
     * @return The y position of the button.
     */
    public abstract int getY();

    /**
     * @return The width of the button.
     */
    public abstract int getWidth();

    /**
     * @return The height of the button.
     */
    public abstract int getHeight();

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public void toggleFlipped(){
        flipped = !flipped;
    }

    public void destroy() {
        setActive(false);
        final Button self = this;
        new Event() {
            @Override
            public void performAction() {
                GameView.getState().getButtons().remove(self);
            }
        };
    }
}
