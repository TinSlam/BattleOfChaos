package com.tinslam.battleheart.gameUtility;

import java.util.HashMap;

public class PathFindingMap{
    private HashMap<String, Node> map = new HashMap<>();

    public void updateClearance(){
        for(Node node : map.values()){
            Node previous = get(node.x - 1, node.y);
            if(previous != null && previous.xClearance != -1){
                node.xClearance = previous.xClearance - 1;
            }else{
                int i = 1;
                while(true){
                    boolean flag = false;
                    for(int j = 0; j < i + 1; j++){
                        if(!containsKey(node.x + j, node.y)){
                            flag = true;
                            break;
                        }
                    }
                    if(flag) break;
                    i++;
                }
                node.xClearance = i;
            }

            previous = get(node.x, node.y - 1);
            if(previous != null && previous.yClearance != -1){
                node.yClearance = previous.yClearance - 1;
            }else{
                int i = 1;
                while(true){
                    boolean flag = false;
                    for(int j = 0; j < i + 1; j++){
                        if(!containsKey(node.x, node.y + j)){
                            flag = true;
                            break;
                        }
                    }
                    if(flag) break;
                    i++;
                }
                node.yClearance = i;
            }
        }
    }

    public boolean containsKey(int x, int y){
        return map.containsKey(String.valueOf(x) + " " + String.valueOf(y));
    }

    public Node get(int x, int y){
        return map.get(String.valueOf(x) + " " + String.valueOf(y));
    }

    public void addWalkable(int x, int y){
        map.put(String.valueOf(x) + " " + String.valueOf(y), new Node(x, y));
    }

    public void removeWalkable(int x, int y){
        map.remove(String.valueOf(x) + " " + String.valueOf(y));
    }
}
