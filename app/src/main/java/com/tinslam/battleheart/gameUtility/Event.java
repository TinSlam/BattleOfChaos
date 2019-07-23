package com.tinslam.battleheart.gameUtility;

import com.tinslam.battleheart.interfaces.ActionInterface;

import java.util.ArrayList;

public abstract class Event implements ActionInterface{
    private static final Object lock = new Object();
    private static ArrayList<Event> events = new ArrayList<>();

    public Event(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized(lock){
                    events.add(Event.this);
                }
            }
        }).start();
    }

    public static void runEvents(){
        synchronized(lock){
            for(Event event : events){
                try{
                    event.performAction();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            events.clear();
        }
    }

    public static void clear(){
        synchronized(lock){
            events.clear();
        }
    }
}
