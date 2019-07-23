package com.tinslam.battleheart.elements3D;

public class Texture{
    private int textureData;
    private int width;
    private int height;

    public Texture(int textureData, int width, int height){
        this.textureData = textureData;
        this.width = width;
        this.height = height;
    }

    public void setTexture(int texture){
        textureData = texture;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getTexture(){
        return textureData;
    }
}
