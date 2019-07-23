package com.tinslam.battleheart.utils;

import android.content.Context;

import com.tinslam.battleheart.base.MyGL2dRenderer;
import com.tinslam.battleheart.elements3D.KingdomManager;
import com.tinslam.battleheart.entities.entities3D.Armory;
import com.tinslam.battleheart.entities.entities3D.Dungeon;
import com.tinslam.battleheart.entities.entities3D.Shop;
import com.tinslam.battleheart.entities.entities3D.Block;
import com.tinslam.battleheart.entities.entities3D.Castle;
import com.tinslam.battleheart.base.ModelData;
import com.tinslam.battleheart.base.MyGLRenderer;
import com.tinslam.battleheart.entities.entities3D.Portal;
import com.tinslam.battleheart.entities.entities3D.Tavern;
import com.tinslam.battleheart.entities.entities3D.TrainingYard;
import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.base.GameView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class that handles files.
 */
public class FileManager{
    public static void parseObj(final int resourceId, ModelData modelData){
        ArrayList<float[]> vertices = new ArrayList<>();
        ArrayList<float[]> textureCoordinates = new ArrayList<>();
        ArrayList<float[]> normals = new ArrayList<>();
        ArrayList<Short[]> faces = new ArrayList<>();
        ArrayList<Short> verticesFoundSoFar = new ArrayList<>();
        boolean newFace = true;
        short[] lastFaces = new short[3];
        short[] lastFacesNewValue = new short[3];

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE;

        try{
            Scanner sc = new Scanner(new InputStreamReader(MyGL2dRenderer.getContext().getResources().openRawResource(resourceId)));

            while(sc.hasNext()){
                String line = sc.nextLine();
                if(line.startsWith("v ")){
                    String[] str = line.split(" ");
                    float x = Float.parseFloat(str[1]), y = Float.parseFloat(str[2]), z = Float.parseFloat(str[3]);
                    if(x < minX) minX = x;
                    if(x > maxX) maxX = x;
                    if(y < minY) minY = y;
                    if(y > maxY) maxY = y;
                    if(z < minZ) minZ = z;
                    if(z > maxZ) maxZ = z;
                    vertices.add(new float[] {x, y, z});
                }else if(line.startsWith("vt ")) {
                    String[] str = line.split(" ");
                    textureCoordinates.add(new float[]{
                            Float.parseFloat(str[1]),
                            1 - Float.parseFloat(str[2])
                    });
                }else if(line.startsWith("vn ")){
                    String[] str = line.split(" ");
                    normals.add(new float[] {
                            Float.parseFloat(str[1]),
                            Float.parseFloat(str[2]),
                            Float.parseFloat(str[3])
                    });
                }else if(line.startsWith("f ")){
                    String[] str = line.split(" ");
                    String[] vertex1 = str[1].split("/");
                    String[] vertex2 = str[2].split("/");
                    String[] vertex3 = str[3].split("/");
                    short index1 = Short.parseShort(vertex1[0]);
                    short index2 = Short.parseShort(vertex2[0]);
                    short index3 = Short.parseShort(vertex3[0]);
                    if(newFace){
                        lastFaces[0] = index1;
                        lastFaces[1] = index2;
                        lastFaces[2] = index3;
                        if(verticesFoundSoFar.contains(index1)){
                            vertices.add(vertices.get(index1 - 1));
                            index1 = (short) vertices.size();
                        }else{
                            verticesFoundSoFar.add(index1);
                        }
                        if(verticesFoundSoFar.contains(index2)){
                            vertices.add(vertices.get(index2 - 1));
                            index2 = (short) vertices.size();
                        }else{
                            verticesFoundSoFar.add(index2);
                        }
                        if(verticesFoundSoFar.contains(index3)){
                            vertices.add(vertices.get(index3 - 1));
                            index3 = (short) vertices.size();
                        }else{
                            verticesFoundSoFar.add(index3);
                        }
                        lastFacesNewValue[0] = index1;
                        lastFacesNewValue[1] = index2;
                        lastFacesNewValue[2] = index3;
                    }else{
                        int ind = 0;
                        boolean flag = false;
                        for(int i = 0; i < 3; i++){
                            if(index1 == lastFaces[i]){
                                flag = true;
                                ind = i;
                                break;
                            }
                        }
                        if(!flag){
                            if(verticesFoundSoFar.contains(index1)){
                                vertices.add(vertices.get(index1 - 1));
                                index1 = (short) vertices.size();
                            }else{
                                verticesFoundSoFar.add(index1);
                            }
                        }else{
                            index1 = lastFacesNewValue[ind];
                        }
                        flag = false;
                        for(int i = 0; i < 3; i++){
                            if(index2 == lastFaces[i]){
                                flag = true;
                                ind = i;
                                break;
                            }
                        }
                        if(!flag){
                            if(verticesFoundSoFar.contains(index2)){
                                vertices.add(vertices.get(index2 - 1));
                                index2 = (short) vertices.size();
                            }else{
                                verticesFoundSoFar.add(index2);
                            }
                        }else{
                            index2 = lastFacesNewValue[ind];
                        }
                        flag = false;
                        for(int i = 0; i < 3; i++){
                            if(index3 == lastFaces[i]){
                                flag = true;
                                ind = i;
                                break;
                            }
                        }
                        if(!flag){
                            if(verticesFoundSoFar.contains(index3)){
                                vertices.add(vertices.get(index3 - 1));
                                index3 = (short) vertices.size();
                            }else{
                                verticesFoundSoFar.add(index3);
                            }
                        }else{
                            index3 = lastFacesNewValue[ind];
                        }
                    }
                    newFace = !newFace;
                    faces.add(new Short[] {
                            index1,
                            Short.parseShort(vertex1[1]),
                            Short.parseShort(vertex1[2]),
                            index2,
                            Short.parseShort(vertex2[1]),
                            Short.parseShort(vertex2[2]),
                            index3,
                            Short.parseShort(vertex3[1]),
                            Short.parseShort(vertex3[2])
                    });
                }
            }

            sc.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        float[] vbo = new float[8 * faces.size() * 3];
        short[] ibo = new short[faces.size() * 3];

        for(int i = 0; i < faces.size(); i++){
            ibo[i * 3] = (short) (faces.get(i)[0] - 1);
            vbo[ibo[i * 3] * 8] = vertices.get(faces.get(i)[0] - 1)[0];
            vbo[ibo[i * 3] * 8 + 1] = vertices.get(faces.get(i)[0] - 1)[1];
            vbo[ibo[i * 3] * 8 + 2] = vertices.get(faces.get(i)[0] - 1)[2];
            vbo[ibo[i * 3] * 8 + 3] = normals.get(faces.get(i)[2] - 1)[0];
            vbo[ibo[i * 3] * 8 + 4] = normals.get(faces.get(i)[2] - 1)[1];
            vbo[ibo[i * 3] * 8 + 5] = normals.get(faces.get(i)[2] - 1)[2];
            vbo[ibo[i * 3] * 8 + 6] = textureCoordinates.get(faces.get(i)[1] - 1)[0];
            vbo[ibo[i * 3] * 8 + 7] = textureCoordinates.get(faces.get(i)[1] - 1)[1];
            ibo[i * 3 + 1] = (short) (faces.get(i)[3] - 1);
            vbo[ibo[i * 3 + 1] * 8] = vertices.get(faces.get(i)[3] - 1)[0];
            vbo[ibo[i * 3 + 1] * 8 + 1] = vertices.get(faces.get(i)[3] - 1)[1];
            vbo[ibo[i * 3 + 1] * 8 + 2] = vertices.get(faces.get(i)[3] - 1)[2];
            vbo[ibo[i * 3 + 1] * 8 + 3] = normals.get(faces.get(i)[5] - 1)[0];
            vbo[ibo[i * 3 + 1] * 8 + 4] = normals.get(faces.get(i)[5] - 1)[1];
            vbo[ibo[i * 3 + 1] * 8 + 5] = normals.get(faces.get(i)[5] - 1)[2];
            vbo[ibo[i * 3 + 1] * 8 + 6] = textureCoordinates.get(faces.get(i)[4] - 1)[0];
            vbo[ibo[i * 3 + 1] * 8 + 7] = textureCoordinates.get(faces.get(i)[4] - 1)[1];
            ibo[i * 3 + 2] = (short) (faces.get(i)[6] - 1);
            vbo[ibo[i * 3 + 2] * 8] = vertices.get(faces.get(i)[6] - 1)[0];
            vbo[ibo[i * 3 + 2] * 8 + 1] = vertices.get(faces.get(i)[6] - 1)[1];
            vbo[ibo[i * 3 + 2] * 8 + 2] = vertices.get(faces.get(i)[6] - 1)[2];
            vbo[ibo[i * 3 + 2] * 8 + 3] = normals.get(faces.get(i)[8] - 1)[0];
            vbo[ibo[i * 3 + 2] * 8 + 4] = normals.get(faces.get(i)[8] - 1)[1];
            vbo[ibo[i * 3 + 2] * 8 + 5] = normals.get(faces.get(i)[8] - 1)[2];
            vbo[ibo[i * 3 + 2] * 8 + 6] = textureCoordinates.get(faces.get(i)[7] - 1)[0];
            vbo[ibo[i * 3 + 2] * 8 + 7] = textureCoordinates.get(faces.get(i)[7] - 1)[1];
        }

        modelData.setWidth(maxX - minX);
        modelData.setHeight(maxY - minY);
        modelData.setDepth(maxZ - minZ);

        modelData.setVBO(vbo);
        modelData.setIBO(ibo);
    }

    /**
     * Loads the kingdom from the file.
     */
    public static void loadKingdom(String path, Context context){
        int width, height;
        try {
            Scanner sc = new Scanner(context.getAssets().open(path));
            width = Integer.parseInt(sc.next());
            height = Integer.parseInt(sc.next());

            KingdomManager.walkable = new boolean[width * 2][height * 2];
            for(int i = 0; i < width * 2; i++){
                for(int j = 0; j < height * 2; j++){
                    KingdomManager.walkable[i][j] = true;
                }
            }

            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    int data;
                    try{
                        data = Integer.parseInt(sc.next());
                    }catch(Exception e){
                        continue;
                    }

                    switch(data){
                        case 1 :
                            new Block(j * 2, i * 2, -2, 0, 0, 0, 2, 2, 2);
                            removeWalkable(j * 2, i * 2, 2, 2);
                            break;

                        case 2 :
                            new Castle(j * 2, i * 2, -8, 0, 0, 0, 8, 8, 8);
                            removeWalkable(j * 2, i * 2, 8, 8);
                            break;

                        case 3 :
                            new Tavern(j * 2, i * 2, -6, 0, 0, 0, 4, 4, 6);
                            removeWalkable(j * 2, i * 2, 4, 4);
                            break;

                        case 4 :
                            new Portal(j * 2, i * 2, -12, 0, 0, 0, 6, 6, 12);
                            removeWalkable(j * 2, i * 2, 6, 6);
                            break;

                        case 5 :
                            new Shop(j * 2, i * 2, -4, 0, 0, 0, 8, 8, 4);
                            removeWalkable(j * 2, i * 2, 8, 8);
                            break;

                        case 6 :
                            new Armory(j * 2, i * 2, -6, 0, 0, 0, 6, 6, 6);
                            removeWalkable(j * 2, i * 2, 6, 6);
                            break;

                        case 7 :
                            new Dungeon(j * 2, i * 2 + 1.2f, -2 + 0.5f, 90, 0, 180, 6, 2, 3);
                            removeWalkable(j * 2, i * 2, 6, 2);
                            break;

                        case 8 :
                            new TrainingYard(j * 2, i * 2, -6, 0, 0, 0, 2, 2, 6);
                            removeWalkable(j * 2, i * 2, 2, 2);
                            break;
                    }
                }
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeWalkable(int x, int y, int width, int height){
        for(int i = x; i < x + width; i++){
            for(int j = y; j < y + height; j++){
                KingdomManager.walkable[i][j] = false;
            }
        }
    }

    /**
     * Loads a byte[][] file and returns the 2D array.
     * Enter -1 as width and height to not check for corrupted file.
     * @param path The file path.
     * @param width The width of the array.
     * @param height The height of the array.
     * @return The 2D array.
     */
    public static byte[][] loadFile(String path, int width, int height) {
        byte[][] tiles = null;
        try {
            Scanner sc = new Scanner(GameView.Context().getAssets().open(path));
            int w = Integer.parseInt(sc.next());
            int h = Integer.parseInt(sc.next());
            if(width != -1 && height != -1){
                if(w != width || height != h) return null;
            }
            width = w;
            height = h;
            tiles = new byte[width][height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    tiles[i][j] = Byte.parseByte(sc.next());
                }
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tiles;
    }

    /**
     * @param path The path of the file to read from.
     * @return An ArrayList of packs of enemies that need to spawn at a level on each wave.
     */
    public static ArrayList<ArrayList<String>> loadSpawns(String path){
        ArrayList<ArrayList<String>> waves = new ArrayList<>();
        try {
            Scanner sc = new Scanner(GameView.Context().getAssets().open(path));
            if(!sc.next().equalsIgnoreCase("!")){
                System.out.println("Corrupted file.");
                return waves;
            }
            while(sc.hasNext()){
                String str;
                ArrayList<String> wave = new ArrayList<>();
                while(!(str = sc.next()).equalsIgnoreCase("!")){
                    wave.add(str);
                    if(!sc.hasNext()) break;
                }
                waves.add(wave);
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return waves;
    }

    /**
     * @return The full string inside a file/resource.
     */
    public static String readTextFileFromRawResource(final Context context, final int resourceId){
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try{
            while((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine);
                body.append('\n');
            }
        }catch(IOException e){
            return null;
        }

        return body.toString();
    }

    public static void loadUnlockedCharacters(ArrayList<String> unlockedCharacters, String path) {
        try {
            Scanner sc = new Scanner(GameView.Context().getAssets().open(path));
            while(sc.hasNext()){
                String character = sc.next();
                unlockedCharacters.add(character);
                PlayerStats.loadUnitStats(character);
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(String fileName) {
        try {
            GameView.Context().getAssets().open(fileName).close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}

