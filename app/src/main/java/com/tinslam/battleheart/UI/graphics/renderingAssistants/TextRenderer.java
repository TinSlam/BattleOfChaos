package com.tinslam.battleheart.UI.graphics.renderingAssistants;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tinslam.battleheart.activities.OpenGL2dActivity;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.states.BattleState;

/**
 * The class that handles rendering text with dynamic size.
 */
public class TextRenderer{
    private static int maxTextViews = 50;
    private static final Object lock = new Object();
    private static TextView[] textViews = new TextView[maxTextViews];
    private static boolean[] freeTextViews = new boolean[maxTextViews];
    private static boolean empty = true;
    private String text;

    static{
        for(int i = 0; i < maxTextViews; i++){
            textViews[i] = new TextView(OpenGL2dActivity.openGL2dActivity);
            freeTextViews[i] = true;
            textViews[i].setVisibility(View.GONE);
            final int finalI = i;
            OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OpenGL2dActivity.openGL2dActivity.addContentView(textViews[finalI], new RelativeLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight()));
                }
            });
        }
    }

    private float x, y;
    private float maxWidth;
    private float maxPaintSize;
    private boolean multiLine = false;
    private TextView textView;
    private int index = -1;
    private Paint.Align align;

    /**
     * Constructor.
     * @param text The text.
     * @param x The x position.
     * @param y The y position.
     * @param maxWidth The maximum width size of the text.
     * @param maxPaintSize The maximum size of the font.
     * @param align The align option for the text.
     */
    public TextRenderer(final String text, float x, float y, final float maxWidth, final float maxPaintSize, final Paint.Align align, final boolean multiLine, final boolean involveCamera){
        this.text = text;
        this.multiLine = multiLine;
        this.x = x;
        this.y = y;
        this.maxWidth = maxWidth;
        this.maxPaintSize = maxPaintSize;
        this.align = align;

        for(int i = 0; i < maxTextViews; i++){
            if(freeTextViews[i]){
                textView = textViews[i];
                index = i;
                freeTextViews[i] = false;
                empty = false;
                break;
            }
        }
        if(textView == null) return;

        textView.setTextColor(Color.BLACK);
        textView.setAlpha(1);

        final float finX = x;
        final float finY = y;
        final TextRenderer self = this;
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.INVISIBLE);
                float x = finX;
                float y = finY;
                float textSize = maxPaintSize;
                textView.setText(text);
                textView.setTextSize(textSize / GameView.density());
                if(!multiLine){
                    int textWidth = (int) textView.getPaint().measureText(text, 0, text.length());
                    while(textWidth > maxWidth){
                        if(textSize <= 1) return;
                        textView.setTextSize(--textSize);
                        textWidth = (int) textView.getPaint().measureText(text, 0, text.length());
                    }

                    switch(align){
                        case CENTER :
                            x -= textWidth / 2;
                            break;

                        case RIGHT :
                            x -= textWidth;
                            break;
                    }
                    textView.measure(0, 0);
                    y -= textView.getMeasuredHeight() / 2;
                    y -= textView.getPaint().descent() / 2;
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        textView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                    }
                    int textWidth = (int) textView.getPaint().measureText(text, 0, text.length());
                    switch(align){
                        case CENTER :
                            x -= textWidth / 2;
                            break;

                        case RIGHT :
                            x -= textWidth;
                            break;
                    }
                    textView.measure(0, 0);
                    y -= textView.getMeasuredHeight() / 2;
                    y -= textView.getPaint().descent() / 2;
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) maxWidth, GameView.getScreenHeight());
                params.leftMargin = (int) x;
                params.topMargin = (int) y;
                if(involveCamera){
                    self.x = x + BattleState.getBattleState().getCameraX();
                    self.y = y + BattleState.getBattleState().getCameraY();
                }else{
                    self.x = x;
                    self.y = y;
                }
                textView.setLayoutParams(params);
            }
        });
    }

    public static boolean isEmpty() {
        return empty;
    }

    public void show(){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textView != null) textView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hide(){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textView != null) textView.setVisibility(View.GONE);
            }
        });
    }

    public void destroy(){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(index != -1){
                    freeTextViews[index] = true;
                    textViews[index].setVisibility(View.GONE);
                }
            }
        });
    }

    public static void clear(){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < maxTextViews; i++){
                    freeTextViews[i] = true;
                    textViews[i].setVisibility(View.GONE);
                }
                empty = true;
            }
        });
    }

    /**
     * Updates the text of the renderer.
     */
    public void setText(final String text){
        if(textView == null) return;
        if(index == -1) return;
//        if(textViews[index] != textView) return;
//        if(textView == null) return;
        final TextRenderer self = this;
        this.text = text;
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int textWidth = (int) textView.getPaint().measureText(textView.getText(), 0, textView.getText().length());
                switch(align){
                    case CENTER :
                        x += textWidth / 2;
                        break;

                    case RIGHT :
                        x += textWidth;
                        break;
                }
                float textSize = maxPaintSize;
                textView.setText(text);
                textView.setTextSize(textSize / GameView.density());
                float x = self.x;
                if(!multiLine){
                    textWidth = (int) textView.getPaint().measureText(text, 0, text.length());
                    while(textWidth > maxWidth){
                        if(textSize <= 1) return;
                        textView.setTextSize(--textSize);
                        textWidth = (int) textView.getPaint().measureText(text, 0, text.length());
                    }
                    switch(align){
                        case CENTER :
                            x -= textWidth / 2;
                            break;

                        case RIGHT :
                            x -= textWidth;
                            break;
                    }

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                    params.leftMargin = (int) x;
                    params.topMargin = (int) y;
                    self.x = x;
                    textView.setLayoutParams(params);
                }else{
                    textWidth = (int) textView.getPaint().measureText(text, 0, text.length());
                    switch(align){
                        case CENTER :
                            x -= textWidth / 2;
                            break;

                        case RIGHT :
                            x -= textWidth;
                            break;
                    }
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) maxWidth, GameView.getScreenHeight());
                    params.leftMargin = (int) x;
                    params.topMargin = (int) y;
                    self.x = x;
                    textView.setLayoutParams(params);
                }
//                textView.setText(textView.getText().toString() + " " + index);
            }
        });
    }

    /**
     * Updates the text to redo the size.
     */
    public void update(){
        if(textView == null) return;
        setText(textView.getText().toString());
    }

    /**
     * Sets the maximum paint size.
     */
    public void setMaxPaintSize(float size) {
        maxPaintSize = size;
    }

    /**
     * Translates the position.
     */
    public void translate(final float dx, final float dy){
        x += dx;
        y += dy;
        if(textView == null) return;
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GameView.getScreenWidth(), GameView.getScreenHeight());
                params.leftMargin = (int) (x);
                params.topMargin = (int) (y);
                textView.setLayoutParams(params);
            }
        });
    }

    public void setTextSize(final float size){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textView != null) textView.setTextSize(size / GameView.density());
            }
        });
    }

    public void setTextColor(final int color){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textView != null) textView.setTextColor(color);
            }
        });
    }

    public void setTextAlpha(final int alpha){
        OpenGL2dActivity.openGL2dActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) textView.setAlpha((float) alpha / 255);
            }
        });
    }

    public int getTextAlpha() {
        if(textView == null) return 0;
        return (int) (textView.getAlpha() * 255);
    }

    public float getTextSize() {
        if(textView == null) return 0;
        return textView.getTextSize() * GameView.density();
    }

    public String getText() {
        return text;
    }
}
