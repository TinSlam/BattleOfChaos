package com.tinslam.battleheart.utils;

import com.tinslam.battleheart.gameUtility.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class MinHeap {
    private ArrayList<Node> list;
    public HashMap<Node, Float> gScore = new HashMap<>();
    public HashMap<Node, Float> fScore = new HashMap<>();

    public MinHeap() {
        this.list = new ArrayList<>();
    }

    public float getFScore(Node node){
        Float score = fScore.get(node);
        if(score == null) score = Float.MAX_VALUE;
        return score;
    }

    public float getGScore(Node node){
        Float score = gScore.get(node);
        if(score == null) score = Float.MAX_VALUE;
        return score;
    }

    public boolean has(Node item){
        for(int i = 0; i < list.size(); i++){
            if(item == list.get(i)) return true;
        }

        return false;
    }

    public void insert(Node item) {

        list.add(item);
        int i = list.size() - 1;
        int parent = parent(i);

        while (parent != i && getFScore(list.get(i)) < getFScore(list.get(parent))) {

            swap(i, parent);
            i = parent;
            parent = parent(i);
        }
    }

    public void buildHeap() {

        for (int i = list.size() / 2; i >= 0; i--) {
            minHeapify(i);
        }
    }

    public Node extractMin() {

        if (list.size() == 0) {

            throw new IllegalStateException("MinHeap is EMPTY");
        } else if (list.size() == 1) {

            return list.remove(0);
        }

        // remove the last item ,and set it as new root
        Node min = list.get(0);
        Node lastItem = list.remove(list.size() - 1);
        list.set(0, lastItem);

        // bubble-down until heap property is maintained
        minHeapify(0);

        // return min key
        return min;
    }

    private void minHeapify(int i) {

        int left = left(i);
        int right = right(i);
        int smallest = -1;

        // find the smallest key between current node and its children.
        if (left <= list.size() - 1 && getFScore(list.get(left)) < getFScore(list.get(i))) {
            smallest = left;
        } else {
            smallest = i;
        }

        if (right <= list.size() - 1 && getFScore(list.get(right)) < getFScore(list.get(smallest))) {
            smallest = right;
        }

        // if the smallest key is not the current key then bubble-down it.
        if (smallest != i) {

            swap(i, smallest);
            minHeapify(smallest);
        }
    }

    public boolean isEmpty() {

        return list.size() == 0;
    }

    private int right(int i) {

        return 2 * i + 2;
    }

    private int left(int i) {

        return 2 * i + 1;
    }

    private int parent(int i) {

        if (i % 2 == 1) {
            return i / 2;
        }

        return (i - 1) / 2;
    }

    private void swap(int i, int parent) {

        Node temp = list.get(parent);
        list.set(parent, list.get(i));
        list.set(i, temp);
    }

    public Node getMin() {
        if(list.isEmpty()) return null;
        return list.get(0);
    }
}