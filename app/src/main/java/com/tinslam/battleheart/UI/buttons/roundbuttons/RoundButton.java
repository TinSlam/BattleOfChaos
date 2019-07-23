package com.tinslam.battleheart.UI.buttons.roundbuttons;

import android.graphics.Paint;

import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.utils.Utils;

/**
 * A class that handles circle shaped buttons.
 */
public abstract class RoundButton extends Button {
    private int x, y, radius;

    /**
     * Constructor.
     * @param x The x position of the center of the circle.
     * @param y The y position of the center of the circle.
     * @param image Image.
     * @param onClickImage Image when pressed.
     * @param manualRender Whether the button is to be rendered manually or automatically.
     */
    protected RoundButton(int x, int y, Texture image, Texture onClickImage, boolean manualRender){
        super(image, onClickImage, manualRender);

        this.x = x;
        this.y = y;
        radius = image.getWidth() / 2;
    }

    /**
     * Constructor.
     * @param x The x position of the center of the circle.
     * @param y The y position of the center of the circle.
     * @param image Image.
     * @param onClickImage Image when pressed.
     * @param text The text to be drawn on the button.
     * @param manualRender Whether the button is to be rendered manually or automatically.
     */
    protected RoundButton(int x, int y, Texture image, Texture onClickImage, String text, boolean manualRender){
        super(image, onClickImage, manualRender);

        this.x = x;
        this.y = y;
        radius = image.getWidth() / 2;
        textRenderer = new TextRenderer(text, x , y, image.getWidth() - 2 * GameView.density() * 4, image.getHeight() / 2, Paint.Align.CENTER, false, false);
    }

    /**
     * Resize the button.
     * The button will also be translated so that the new button and the old one have the same center position.
     */
    @Override
    public void resizeImage(int width, int height){
        resizeImage(width / 2);
    }

    /**
     * Checks whether the button contains the point (mx, my).
     */
    @Override
    public boolean isTouched(int mx, int my){
        return Utils.distance(mx, my, x, y) < radius;

    }

    /**
     * Renders the button.
     * Draws the image of the button and the text if it has any.
     */
    @Override
    public void render(){
        MyGL2dRenderer.drawLabel(x - radius, y - radius, 2 * radius, 2 * radius, currentImage.getTexture(), 255);
    }

    /**
     * Resize the button.
     * @param radius The new radius.
     */
    private void resizeImage(int radius){
        this.radius = radius;
        int t = 2;
        if(currentImage == image){
            t = 1;
        }
        setImage(new Texture(image.getTexture(), radius * 2, radius * 2));
        setImageOnClick(new Texture(imageOnClick.getTexture(), radius * 2, radius * 2));
        if(t == 1){
            currentImage = image;
        }else{
            currentImage = imageOnClick;
        }
    }

    /**
     * Translates the button.
     */
    @Override
    public void translate(int dx, int dy){
        x += dx;
        y += dy;
        if(textRenderer != null) textRenderer.translate(dx, dy);
    }

    /**
     *
     * @return The center x position of the center of the button.
     */
    @Override
    public int getX(){
        return x;
    }

    /**
     * Sets the center x position of the center of the button.
     */
    public void setX(int x){
        this.x = x;
    }

    /**
     *
     * @return The center y position of the center of the button.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * @return The width.
     */
    @Override
    public int getWidth(){
        return radius / 2;
    }

    /**
     * @return The height.
     */
    @Override
    public int getHeight(){
        return radius / 2;
    }

    /**
     * Sets the center y position of the center of the button.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the center position of the button.
     */
    @Override
    public void setPosition(int x, int y){
        setX(x);
        setY(y);
    }
}
