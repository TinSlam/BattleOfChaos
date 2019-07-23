package com.tinslam.battleheart.gameUtility;

public class Node{
    public int x = 0;
    public int y = 0;
    public int xClearance = -1;
    public int yClearance = -1;
    boolean walkable = false;

    Node(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Node(int x, int y, boolean walkable){
        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }

    public void setWalkable(boolean walkable){
        this.walkable = walkable;
    }

    public boolean isWalkable(){
        return walkable;
    }
}