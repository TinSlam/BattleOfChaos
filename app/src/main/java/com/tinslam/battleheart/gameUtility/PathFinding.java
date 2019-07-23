package com.tinslam.battleheart.gameUtility;

import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.utils.MinHeap;
import com.tinslam.battleheart.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PathFinding{
    private static HashMap<Unit, Boolean> stopMap = new HashMap<>();
    private static final Object queueLock = new Object();

    /**
     * As the first index of the returned array returns an ArrayList of integers, each specifying a waypoint of the path. If path is not found a path to the closest viable node is returned.
     * The second index returns a boolean determining whether the original path was found or not.
     * @param xStart The start x.
     * @param yStart The start y.
     * @param xEnd The end x.
     * @param yEnd The end y.
     * @param map The pathfinding map.
     */
    public static Object[] findPath(int xStart, int yStart, int xEnd, int yEnd, PathFindingMap map, int maximumSearchRadius, final Unit unit){
        synchronized(queueLock){
            stopMap.put(unit, true);
        }
        Object[] objects;
        synchronized(unit){
            synchronized(queueLock){
                stopMap.put(unit, false);
            }
            objects = new Object[0];
            try {
                objects = aStar(xStart, yStart, xEnd, yEnd, map, maximumSearchRadius, unit);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized(queueLock){
                stopMap.remove(unit);
            }
        }
        return objects;
    }

    private static Object[] aStar(int xStart, int yStart, int xEnd, int yEnd, PathFindingMap map, int maximumSearchRadius, final Unit unit) throws Exception{
        ArrayList<int[]> path = new ArrayList<>();
        MinHeap openSet = new MinHeap();
        HashMap<Node, Boolean> closedSet = new HashMap<>();
        HashMap<Node, Node> cameFrom = new HashMap<>();

        Node start = map.get(xStart, yStart);
        Node end = map.get(xEnd, yEnd);

        int xClearance = unit.getXClearance();
        int yClearance = unit.getYClearance();

        if(end == null || end.xClearance < xClearance || end.yClearance < yClearance) maximumSearchRadius = 20;

        Node closestNode = start;
        float closestDistance = Utils.distance((float) xStart, yStart, xEnd, yEnd);

        if(start == null){
            System.out.println("Attempt to move from solid path : " + xStart + " " + yStart);
            if(map.containsKey(xStart - 1, yStart)){
                path.add(new int[] {xStart - 1, yStart});
            }else if(map.containsKey(xStart + 1, yStart)){
                path.add(new int[] {xStart + 1, yStart});
            }else if(map.containsKey(xStart, yStart + 1)){
                path.add(new int[] {xStart, yStart + 1});
            }else if(map.containsKey(xStart, yStart - 1)){
                path.add(new int[] {xStart, yStart - 1});
            }else{
                path.add(new int[] {xEnd, yEnd});
            }
            return new Object[] {path, true};
        }

        openSet.insert(start);
        openSet.gScore.put(start, 0f);
        openSet.fScore.put(start, heuristicValue(xStart, yStart, xEnd, yEnd));

        while(!openSet.isEmpty()){
            synchronized(queueLock){
                if(stopMap.get(unit)) return null;
            }

            Node current = openSet.extractMin();
            if(current == null) break;
            if(current == end){
                if(end.xClearance == xClearance || end.yClearance == yClearance){
                    return new Object[] {constructPath(cameFrom, current), false};
                }else{
                    return new Object[] {constructPath(cameFrom, current), true};
                }
            }

            closedSet.put(current, true);

            ArrayList<Node> neighbors = new ArrayList<>();
            Node temp = map.get(current.x + 1, current.y);
            if(temp != null && temp.xClearance >= xClearance && temp.yClearance >= yClearance && Math.abs(temp.x - start.x) <= maximumSearchRadius && Math.abs(temp.y - start.y) <= maximumSearchRadius) neighbors.add(temp);
            temp = map.get(current.x - 1, current.y);
            if(temp != null && temp.xClearance >= xClearance && temp.yClearance >= yClearance &&  Math.abs(temp.x - start.x) <= maximumSearchRadius && Math.abs(temp.y - start.y) <= maximumSearchRadius) neighbors.add(temp);
            temp = map.get(current.x, current.y + 1);
            if(temp != null && temp.xClearance >= xClearance && temp.yClearance >= yClearance &&  Math.abs(temp.x - start.x) <= maximumSearchRadius && Math.abs(temp.y - start.y) <= maximumSearchRadius) neighbors.add(temp);
            temp = map.get(current.x, current.y - 1);
            if(temp != null && temp.xClearance >= xClearance && temp.yClearance >= yClearance &&  Math.abs(temp.x - start.x) <= maximumSearchRadius && Math.abs(temp.y - start.y) <= maximumSearchRadius) neighbors.add(temp);

            for(Node node : neighbors){
                if(closedSet.containsKey(node)) continue;

                float tentativeGScore = openSet.getGScore(current) + 1;
                try{
                    if(tentativeGScore >= openSet.getGScore(node)) continue;
                }catch(Exception ignored){}

                cameFrom.put(node, current);
                openSet.gScore.put(node, tentativeGScore);
                openSet.fScore.put(node, tentativeGScore + heuristicValue(node.x, node.y, xEnd, yEnd));
                float distance = Utils.distance((float) node.x, node.y, xEnd, yEnd);
                if(distance < closestDistance){
                    closestNode = node;
                    closestDistance = distance;
                }
                if(!openSet.has(node)) openSet.insert(node);
                openSet.buildHeap();
            }
        }
        return new Object[] {constructPath(cameFrom, closestNode), false};
    }

    private static ArrayList<int[]> constructPath(HashMap<Node, Node> cameFrom, Node current){
        ArrayList<int[]> path = new ArrayList<>();
        Stack<int[]> stack = new Stack<>();
        while(current != null){
            stack.push(new int[] {current.x, current.y});
            current = cameFrom.get(current);
        }
        while(!stack.empty()){
            path.add(stack.pop());
        }
        return path;
    }

    private static float heuristicValue(int x1, int y1, int x2, int y2){
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
