package com.tinslam.battleheart.UI.buttons.rectanglebuttons;

import android.graphics.Paint;
import android.graphics.Rect;

import com.tinslam.battleheart.UI.buttons.Button;
import com.tinslam.battleheart.UI.graphics.renderingAssistants.TextRenderer;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.utils.Utils;

/**
 * A class that handles rectangle shaped buttons.
 */
public abstract class RectangleButton extends Button {
    private Rect rect;
    private Paint fontPaint = new Paint();

    /**
     * Constructor.
     * @param x Left.
     * @param y Top.
     * @param image Image.
     * @param imageOnClick Image when pressed.
     * @param manualRender Whether the button is to be rendered manually or automatically.
     */
    protected RectangleButton(int x, int y, Texture image, Texture imageOnClick, boolean manualRender) {
        super(image, imageOnClick, manualRender);

        rect = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
    }

    /**
     * Constructor.
     * @param rect Rectangle.
     */
    public RectangleButton(Rect rect){
        super(null, null, false);

        this.rect = new Rect(0, 0, 0, 0);
        this.rect.set(rect);
    }

    /**
     * Constructor.
     * @param x Left.
     * @param y Right.
     * @param image Image.
     * @param imageOnClick Image when pressed.
     * @param string Text to draw on the button.
     * @param manualRender Whether the button is to be drawn manually or automatically.
     */
    protected RectangleButton(int x, int y, Texture image, Texture imageOnClick, String string, boolean manualRender) {
        super(image, imageOnClick, manualRender);

        textRenderer = new TextRenderer(string, x + image.getWidth() / 2, y + image.getHeight() / 2, image.getWidth() - 16 * GameView.density(), image.getHeight() / 6 * GameView.density(), Paint.Align.CENTER, false, false);
        rect = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
    }

    /**
     * Constructor.
     * @param x Left.
     * @param y Top.
     * @param width Width.
     * @param height Height.
     * @param image Image.
     * @param imageOnClick Image while pressed.
     * @param string Text to show.
     * @param manualRender Manual rendering or automatically.
     */
    protected RectangleButton(int x, int y, int width, int height, int image, int imageOnClick, String string, boolean manualRender) {
        super(new Texture(image, width, height), new Texture(imageOnClick, width, height), manualRender);

        textRenderer = new TextRenderer(string, x + width / 2, y + height / 2, width - 16 * GameView.density(), height / 6 * GameView.density(), Paint.Align.CENTER, false, false);
        rect = new Rect(x, y, x + width, y + height);
    }

    /**
     * Constructor.
     * @param x Left.
     * @param y Top.
     * @param width Width.
     * @param height Height.
     * @param image Image.
     * @param imageOnClick Image while pressed.
     * @param manualRender Manual rendering or automatically.
     */
    protected RectangleButton(int x, int y, int width, int height, int image, int imageOnClick, boolean manualRender) {
        super(new Texture(image, width, height), new Texture(imageOnClick, width, height), manualRender);

        rect = new Rect(x, y, x + width, y + height);
    }

    /**
     * Checks whether the button contains the point (x, y).
     */
    @Override
    public boolean isTouched(int x, int y){
        return rect.contains(x, y);
    }

    /**
     * Renders the button.
     * Draws the image and the text if it has one.
     */
    @Override
    public void render(){
        if(flipped){
            float[] matrix = {getX() + getWidth(), getY(), -getWidth(), getHeight(), 0};
            MyGL2dRenderer.drawLabel(currentImage.getTexture(), 255, matrix);
        }else{
            MyGL2dRenderer.drawLabel(getX(), getY(), getWidth(), getHeight(), currentImage.getTexture(), 255);
        }
    }

    /**
     * Translates the button.
     */
    @Override
    public void translate(int dx, int dy){
        rect = Utils.translateRect(rect, dx, dy);
        if(textRenderer != null) textRenderer.translate(dx, dy);
    }

    /**
     * Resize the button.
     * The button will also be translated so that the new button and the old one have the same center position.
     */
    @Override
    public void resizeImage(int width, int height){
        int hTranslation = (image.getWidth() - width) / 2;
        int vTranslation = (image.getHeight() - height) / 2;
        rect.set(rect.left + hTranslation, rect.top + vTranslation, rect.left + width + hTranslation, rect.top + height + vTranslation);
        int t = 2;
        if(currentImage == image){
            t = 1;
        }
        setImage(new Texture(image.getTexture(), width, height));
        setImageOnClick(new Texture(imageOnClick.getTexture(), width, height));
        if(t == 1){
            currentImage = image;
        }else{
            currentImage = imageOnClick;
        }
        fontPaint.setTextSize(image.getHeight() / 2);
    }

    /**
     * Repositions the button.
     * @param x The new x position.
     * @param y The new y position.
     */
    private void reposition(int x, int y){
        rect.set(x, y, x + image.getWidth(), y + image.getHeight());
    }

    /**
     * Sets the position of the button.
     */
    @Override
    public void setPosition(int x, int y){
        if(textRenderer != null) textRenderer.translate(x - getX(), y - getY());
        reposition(x, y);
    }

    /**
     * @return The width.
     */
    @Override
    public int getWidth(){
        return rect.width();
    }

    /**
     * @return The height.
     */
    @Override
    public int getHeight(){
        return rect.height();
    }

    /**
     * @return The x position.
     */
    @Override
    public int getX(){
        return rect.left;
    }

    /**
     * @return The y position.
     */
    @Override
    public int getY(){
        return rect.top;
    }
}
