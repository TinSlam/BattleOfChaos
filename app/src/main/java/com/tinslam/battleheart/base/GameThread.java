package com.tinslam.battleheart.base;

/**
 * The main thread that the game runs on. Includes the game loop and the tick and render methods.
 */
public class GameThread{
    public static final int maxFps = 45;
    private static boolean paused = false;
    public static float avgFps = 0;

    /**
     * Constructor.
     */
    GameThread(){
        super();
    }

    /**
     * Pauses the main thread.
     */
    static void pauseThread(){
        paused = true;
    }

    /**
     * Resumes the main thread.
     */
    static void resumeThread(){
        paused = false;
    }

    private static long totalTime = 0;
    private static int frameViewCount = 0;

    public static void tick() {
        long startTime;
        long timeMillis;
        long waitTime;
        long targetTime = 1000 / maxFps;

        if(paused){
            return;
        }
        startTime = System.nanoTime();

        try{
            GameView.gameView.update();
        }catch(Exception e){
            e.printStackTrace();
        }
        timeMillis = (System.nanoTime() - startTime) / 1000000;
        waitTime = targetTime - timeMillis;
        try{
            if(waitTime > 0){
                Thread.sleep(waitTime);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        totalTime += System.nanoTime() - startTime;
        frameViewCount++;

        if(frameViewCount == maxFps){
            double averageFps = 1000 / ((totalTime / frameViewCount) / 1000000);
            frameViewCount = 0;
            totalTime = 0;
            avgFps = (float) averageFps;
            System.out.println("Average FPS : " + averageFps);
        }
    }
}