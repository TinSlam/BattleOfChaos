package com.tinslam.battleheart.gameUtility;

import android.content.Context;
import android.graphics.Bitmap;

import com.tinslam.battleheart.UI.graphics.Animations.Animation;
import com.tinslam.battleheart.UI.graphics.Animations.AnimationLoader;
import com.tinslam.battleheart.UI.graphics.Image;
import com.tinslam.battleheart.base.GameView;
import com.tinslam.battleheart.elements3D.Texture;
import com.tinslam.battleheart.elements3D.TextureData;
import com.tinslam.battleheart.items.Amulet;
import com.tinslam.battleheart.items.Armor;
import com.tinslam.battleheart.items.Boots;
import com.tinslam.battleheart.items.Helmet;
import com.tinslam.battleheart.items.Item;
import com.tinslam.battleheart.items.Weapon;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.utils.FileManager;
import com.tinslam.battleheart.utils.Utils;
import com.tinslam.battleheart.utils.constants.Consts;
import com.tinslam.battleheart.utils.constants.FileConsts;
import com.tinslam.battleheart.utils.constants.NameConsts;
import com.tinslam.battleheart.utils.constants.SpellConsts;
import com.tinslam.battleheart.utils.constants.UnitConsts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class that contains all the information of the account.
 */
public class PlayerStats {
    public static final Object lock = new Object();
    public static ArrayList<Item> items = new ArrayList<>();
    public static ArrayList<String> spells = new ArrayList<>();

    public static int gold;
    private static int lastLevelUnlocked;

    private static Texture portraitKnight, portraitPriest, portraitArcher;

    public static float knightHp, knightDamage, knightArmor, knightSpeed, knightAttackRange;
    public static int knightAttackCd;
    public static int knightAggroRange;
    private static int knightExp;
    private static int knightLvl;
    public static float knightExtraHp, knightExtraDamage, knightExtraArmor, knightExtraSpeed, knightExtraAttackRange;
    public static int knightExtraAttackCd;
    public static ArrayList<String> knightSpells = new ArrayList<>();

    public static float priestHp, priestDamage, priestArmor, priestSpeed, priestAttackRange;
    public static int priestAttackCd;
    public static int priestAggroRange;
    private static int priestExp;
    private static int priestLvl;
    public static float priestExtraHp, priestExtraDamage, priestExtraArmor, priestExtraSpeed, priestExtraAttackRange;
    public static int priestExtraAttackCd;
    public static ArrayList<String> priestSpells = new ArrayList<>();

    public static float archerHp, archerDamage, archerArmor, archerSpeed, archerAttackRange;
    public static int archerAttackCd;
    public static int archerAggroRange;
    private static int archerExp;
    private static int archerLvl;
    public static float archerExtraHp, archerExtraDamage, archerExtraArmor, archerExtraSpeed, archerExtraAttackRange;
    public static int archerExtraAttackCd;
    private static float archerProjectileSpeed;
    public static ArrayList<String> archerSpells = new ArrayList<>();

    private static final Object unlockedCharactersLock = new Object();
    private static ArrayList<String> unlockedCharacters = new ArrayList<>();

    /**
     * Is called when the unit levels up.
     */
    private static void knightLvlUp(){
        knightDamage += UnitConsts.KNIGHT_DAMAGE_GROWTH;
        knightArmor += UnitConsts.KNIGHT_ARMOR_GROWTH;
        knightHp += UnitConsts.KNIGHT_HP_GROWTH;
        knightLvl++;
        if(knightLvl == SpellConsts.TAUNT_LVL_REQUIRED){
            String spell = "com.tinslam.battleheart.spells.Taunt";
            spells.add(spell);
            knightSpells.add(spell);
            updateSpells();
        }
    }

    /**
     * Is called when the unit levels up.
     */
    private static void archerLvlUp(){
        archerDamage += UnitConsts.ARCHER_DAMAGE_GROWTH;
        archerArmor += UnitConsts.ARCHER_ARMOR_GROWTH;
        archerHp += UnitConsts.ARCHER_HP_GROWTH;
        archerLvl++;
        if(archerLvl == SpellConsts.POWERSHOT_LVL_REQUIRED){
            String spell = "com.tinslam.battleheart.spells.Powershot";
            spells.add(spell);
            archerSpells.add(spell);
            updateSpells();
        }
    }

    /**
     * Is called when the unit levels up.
     */
    private static void priestLvlUp(){
        priestDamage += UnitConsts.PRIEST_DAMAGE_GROWTH;
        priestArmor += UnitConsts.PRIEST_ARMOR_GROWTH;
        priestHp += UnitConsts.PRIEST_HP_GROWTH;
        priestLvl++;
        if(priestLvl == SpellConsts.HEAL_AOE_LVL_REQUIRED){
            String spell = "com.tinslam.battleheart.spells.HealAOE";
            spells.add(spell);
            priestSpells.add(spell);
            updateSpells();
        }
    }

    /**
     * Resets the character to base stats.
     */
    private static void reset(String character){
        switch(character){
            case NameConsts.KNIGHT :
                knightHp = UnitConsts.KNIGHT_HP;
                knightDamage = UnitConsts.KNIGHT_DAMAGE;
                knightArmor = UnitConsts.KNIGHT_ARMOR;
                knightSpeed = UnitConsts.KNIGHT_SPEED;
                knightAttackRange = UnitConsts.KNIGHT_ATTACK_RANGE;
                knightAggroRange = UnitConsts.KNIGHT_AGGRO_RANGE;
                knightAttackCd = UnitConsts.KNIGHT_ATTACK_CD;
                knightExp = 0;
                knightLvl = 1;
                knightSpells.clear();
                removeItems(character);
                updateSpells();
                updateKnightStats();
                break;

            case NameConsts.PRIEST :
                priestHp = UnitConsts.PRIEST_HP;
                priestDamage = UnitConsts.PRIEST_DAMAGE;
                priestArmor = UnitConsts.PRIEST_ARMOR;
                priestSpeed = UnitConsts.PRIEST_SPEED;
                priestAttackRange = UnitConsts.PRIEST_ATTACK_RANGE;
                priestAggroRange = UnitConsts.PRIEST_AGGRO_RANGE;
                priestAttackCd = UnitConsts.PRIEST_ATTACK_CD;
                priestExp = 0;
                priestLvl = 1;
                removeItems(character);
                priestSpells.clear();
                updateSpells();
                updatePriestStats();
                break;

            case NameConsts.ARCHER :
                archerHp = UnitConsts.ARCHER_HP;
                archerDamage = UnitConsts.ARCHER_DAMAGE;
                archerArmor = UnitConsts.ARCHER_ARMOR;
                archerSpeed = UnitConsts.ARCHER_SPEED;
                archerAttackRange = UnitConsts.ARCHER_ATTACK_RANGE;
                archerAggroRange = UnitConsts.ARCHER_AGGRO_RANGE;
                archerAttackCd = UnitConsts.ARCHER_ATTACK_CD;
                archerExp = 0;
                archerLvl = 1;
                archerProjectileSpeed = UnitConsts.ARCHER_PROJECTILE_SPEED;
                removeItems(character);
                archerSpells.clear();
                updateSpells();
                updateArcherStats();
                break;

            default :
                System.out.println("Resetting a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Removes an item from the account.
     * Unequips it beforehand.
     */
    private static void removeItems(String character){
        for(Item x : items){
            if(x.getCarrier().equalsIgnoreCase(character)) x.unequip();
        }
        updateItems();
    }

    /**
     * Updates the stats of the unit.
     * hp, damage, armor, speed, attack_range, attack_cd, aggro_range, exp.
     */
    private static void updateKnightStats(){
        try{
            PrintWriter writer = new PrintWriter(GameView.Context().openFileOutput(NameConsts.KNIGHT + FileConsts.CHARACTERS_FILES_EXTENSION, Context.MODE_PRIVATE));
            writer.println(NameConsts.HP + " " + knightHp);
            writer.println(NameConsts.DAMAGE + " " + knightDamage);
            writer.println(NameConsts.ARMOR + " " + knightArmor);
            writer.println(NameConsts.SPEED + " " + knightSpeed);
            writer.println(NameConsts.ATTACK_RANGE + " " + knightAttackRange);
            writer.println(NameConsts.ATTACK_CD + " " + knightAttackCd);
            writer.println(NameConsts.AGGRO_RANGE + " " + knightAggroRange);
            writer.println(NameConsts.EXP + " " + knightExp);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Updates the stats of the unit.
     * hp, damage, armor, speed, attack_range, attack_cd, aggro_range, exp, projectile_speed.
     */
    private static void updateArcherStats(){
        try{
            PrintWriter writer = new PrintWriter(GameView.Context().openFileOutput(NameConsts.ARCHER + FileConsts.CHARACTERS_FILES_EXTENSION, Context.MODE_PRIVATE));
            writer.println(NameConsts.HP + " " + archerHp);
            writer.println(NameConsts.DAMAGE + " " + archerDamage);
            writer.println(NameConsts.ARMOR + " " + archerArmor);
            writer.println(NameConsts.SPEED + " " + archerSpeed);
            writer.println(NameConsts.ATTACK_RANGE + " " + archerAttackRange);
            writer.println(NameConsts.ATTACK_CD + " " + archerAttackCd);
            writer.println(NameConsts.AGGRO_RANGE + " " + archerAggroRange);
            writer.println(NameConsts.EXP + " " + archerExp);
            writer.println(NameConsts.PROJECTILE_SPEED + " " + archerProjectileSpeed);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Updates the stats of the unit.
     * hp, damage, armor, speed, attack_range, attack_cd, aggro_range, exp.
     */
    private static void updatePriestStats(){
        try{
            PrintWriter writer = new PrintWriter(GameView.Context().openFileOutput(NameConsts.PRIEST + FileConsts.CHARACTERS_FILES_EXTENSION, Context.MODE_PRIVATE));
            writer.println(NameConsts.HP + " " + priestHp);
            writer.println(NameConsts.DAMAGE + " " + priestDamage);
            writer.println(NameConsts.ARMOR + " " + priestArmor);
            writer.println(NameConsts.SPEED + " " + priestSpeed);
            writer.println(NameConsts.ATTACK_RANGE + " " + priestAttackRange);
            writer.println(NameConsts.ATTACK_CD + " " + priestAttackCd);
            writer.println(NameConsts.AGGRO_RANGE + " " + priestAggroRange);
            writer.println(NameConsts.EXP + " " + priestExp);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Updates the stats of the account.
     * gold, last_level_unlocked.
     */
    public static void updateAccountStats(){
        try{
            PrintWriter writer = new PrintWriter(GameView.Context().openFileOutput(FileConsts.ACCOUNT_STATS_FILE_NAME, Context.MODE_PRIVATE));
            writer.println(NameConsts.GOLD + " " + gold);
            writer.println(NameConsts.LAST_LEVEL_UNLOCKED + " " + lastLevelUnlocked);
            writer.println(NameConsts.COMMAND_INPUT + " " + BattleState.inputCommand);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Updates the items that the player owns.
     * Saving the equipped state of each.
     */
    public static void updateItems(){
        try{
            PrintWriter writer = new PrintWriter(GameView.Context().openFileOutput(FileConsts.BOUGHT_ITEMS_FILE_NAME, Context.MODE_PRIVATE));
            for(Item x : items){
                if(x instanceof Boots){
                    writer.println("com.tinslam.battleheart.items.boots." + x.getClass().getSimpleName() + " " + x.getCarrier());
                }else if(x instanceof Weapon){
                    writer.println("com.tinslam.battleheart.items.weapons." + x.getClass().getSimpleName() + " " + x.getCarrier());
                }else if(x instanceof Armor){
                    writer.println("com.tinslam.battleheart.items.armors." + x.getClass().getSimpleName() + " " + x.getCarrier());
                }else if(x instanceof Helmet){
                    writer.println("com.tinslam.battleheart.items.helmets." + x.getClass().getSimpleName() + " " + x.getCarrier());
                }else if(x instanceof Amulet){
                    writer.println("com.tinslam.battleheart.items.amulets." + x.getClass().getSimpleName() + " " + x.getCarrier());
                }
            }
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Updates the spells that the player owns.
     * Saving the equipped state of each.
     */
    private static void updateSpells(){
        try{
            PrintWriter writer = new PrintWriter(GameView.Context().openFileOutput(FileConsts.UNLOCKED_SPELLS_FILE_NAME, Context.MODE_PRIVATE));
            String character = NameConsts.KNIGHT;
            for(String x : spells){
                if(knightSpells.contains(x)){
                    character = NameConsts.KNIGHT;
                }else if(priestSpells.contains(x)){
                    character = NameConsts.PRIEST;
                }else if(archerSpells.contains(x)){
                    character = NameConsts.ARCHER;
                }
                writer.println(x + " " + character);
            }
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Resets the following : gold, last_level_unlocked.
     */
    private static void resetAccount(){
        gold = 0;
        lastLevelUnlocked = 1;
        updateAccountStats();
    }

    /**
     * Clears all the items the player owns.
     */
    private static void resetItems(){
        items.clear();
        updateItems();
    }

    /**
     * Clears all spells the player has unlocked.
     */
    private static void resetSpells(){
        archerSpells.clear();
        knightSpells.clear();
        priestSpells.clear();
        spells.clear();
        updateSpells();
    }

    /**
     * Completely resets the progress.
     * The gold, characters, items, level progress and etc.
     */
    public static void resetAll(){
        reset(NameConsts.KNIGHT);
        reset(NameConsts.PRIEST);
        reset(NameConsts.ARCHER);
        resetAccount();
        resetItems();
        resetSpells();
    }

    /**
     * Loads the account information.
     */
    public static void load() {
        loadAccountStats();
        loadItems();
        FileManager.loadUnlockedCharacters(unlockedCharacters, FileConsts.UNLOCKED_CHARACTERS_FILE_NAME);

        knightLvl = Utils.getLevelFromExp(knightExp);
        priestLvl = Utils.getLevelFromExp(priestExp);
        archerLvl = Utils.getLevelFromExp(archerExp);

        loadSpells();
    }

    /**
     * Loads the portraits of the characters.
     */
    public static void loadPortraits(){
        portraitKnight = new Texture(AnimationLoader.knightIdleRight[0], Consts.PORTRAIT_WIDTH, Consts.PORTRAIT_HEIGHT);
        portraitPriest = new Texture(AnimationLoader.priestMoveRight[0], Consts.PORTRAIT_WIDTH, Consts.PORTRAIT_HEIGHT);
        portraitArcher = new Texture(AnimationLoader.archerMoveRight[0], Consts.PORTRAIT_WIDTH, Consts.PORTRAIT_HEIGHT);
    }

    /**
     * Loads the stats of the unit from the file.
     */
    public static void loadUnitStats(String character){
        try{
            Scanner sc = new Scanner(GameView.Context().openFileInput(character + FileConsts.CHARACTERS_FILES_EXTENSION));
            String line;

            while(sc.hasNext()){
                line = sc.nextLine();
                String[] tokens = line.split(" ");
                switch(tokens[0]){
                    case NameConsts.HP :
                        setUnitHp(character, Float.parseFloat(tokens[1]));
                        break;

                    case NameConsts.DAMAGE :
                        setUnitDamage(character, Float.parseFloat(tokens[1]));
                        break;

                    case NameConsts.ARMOR :
                        setUnitArmor(character, Float.parseFloat(tokens[1]));

                    case NameConsts.SPEED :
                        setUnitSpeed(character, Float.parseFloat(tokens[1]));
                        break;

                    case NameConsts.ATTACK_RANGE :
                        setUnitAttackRange(character, Float.parseFloat(tokens[1]));
                        break;

                    case NameConsts.ATTACK_CD :
                        setUnitAttackCd(character, Integer.parseInt(tokens[1]));
                        break;

                    case NameConsts.AGGRO_RANGE :
                        setUnitAggroRange(character, Integer.parseInt(tokens[1]));
                        break;

                    case NameConsts.EXP :
                        setUnitExp(character, Integer.parseInt(tokens[1]));
                        break;

                    case NameConsts.PROJECTILE_SPEED :
                        setUnitProjectileSpeed(character, Float.parseFloat(tokens[1]));
                        break;

                    default :
                        System.out.println("Unit stat not stored : " + tokens[0] + " for " + character);
                }
            }

            sc.close();
        }catch(FileNotFoundException e){
            reset(character);
        }
    }

    /**
     * Loads the items the player owns from the file.
     */
    private static void loadItems(){
        try{
            Scanner sc = new Scanner(GameView.Context().openFileInput(FileConsts.BOUGHT_ITEMS_FILE_NAME));
            String line;

            while(sc.hasNext()) {
                line = sc.nextLine();
                String[] tokens = line.split(" ");
                try{
                    Class<?> itemClass = Class.forName(tokens[0]);
                    Constructor constructor = itemClass.getConstructor(String.class);
                    Item item = (Item) constructor.newInstance((Object) "");
                    items.add(item);
                    try{
                        item.equip(tokens[1]);
                    }catch(Exception ignored){}
                }catch(ClassNotFoundException e){
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            sc.close();
        }catch(FileNotFoundException e){
            resetItems();
        }
    }

    /**
     * Loads the spells the player owns from the file.
     */
    private static void loadSpells(){
        try{
            Scanner sc = new Scanner(GameView.Context().openFileInput(FileConsts.UNLOCKED_SPELLS_FILE_NAME));
            String line;
            boolean changedPowershotName = false;

            while(sc.hasNext()) {
                line = sc.nextLine();
                String[] tokens = line.split(" ");
                if(tokens[1].equalsIgnoreCase(NameConsts.KNIGHT)){
                    knightSpells.add(tokens[0]);
                }else if(tokens[1].equalsIgnoreCase(NameConsts.PRIEST)){
                    priestSpells.add(tokens[0]);
                }else if(tokens[1].equalsIgnoreCase(NameConsts.ARCHER)){
                    if(tokens[0].equals("com.tinslam.battleheart.spells.PowerShot")){
                        tokens[0] = "com.tinslam.battleheart.spells.Powershot";
                        changedPowershotName = true;
                    }
                    archerSpells.add(tokens[0]);
                }
                spells.add(tokens[0]);
            }
            sc.close();
            if(changedPowershotName){
                updateSpells();
            }
        }catch(FileNotFoundException e){
            resetSpells();
        }
    }

    /**
     * Loads the following stats from the file.
     * gold, last_level_unlocked.
     */
    private static void loadAccountStats(){
        try{
            Scanner sc = new Scanner(GameView.Context().openFileInput(FileConsts.ACCOUNT_STATS_FILE_NAME));
            String line;

            while(sc.hasNext()){
                line = sc.nextLine();
                String[] tokens = line.split(" ");
                switch(tokens[0]){
                    case NameConsts.GOLD :
                        gold = Integer.parseInt(tokens[1]);
                        break;

                    case NameConsts.LAST_LEVEL_UNLOCKED :
                        lastLevelUnlocked = Integer.parseInt(tokens[1]);
                        break;

                    case NameConsts.COMMAND_INPUT :
//                        BattleState.inputCommand = Byte.parseByte(tokens[1]);
                        BattleState.inputCommand = Consts.INPUT_COMMAND_POINT;
                        break;
                }
            }
            sc.close();
        }catch(FileNotFoundException e){
            resetAccount();
        }
    }

    /**
     * @return The said bonus stat of the given character.
     */
    public static float getUnitExtraHp(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightExtraHp;

            case NameConsts.PRIEST :
                return PlayerStats.priestExtraHp;

            case NameConsts.ARCHER :
                return PlayerStats.archerExtraHp;

            default :
                System.out.println("Getting the extra hp of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said bonus stat of the given character.
     */
    public static float getUnitExtraDamage(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightExtraDamage;

            case NameConsts.PRIEST :
                return PlayerStats.priestExtraDamage;

            case NameConsts.ARCHER :
                return PlayerStats.archerExtraDamage;

            default :
                System.out.println("Getting the extra damage of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said bonus stat of the given character.
     */
    public static float getUnitExtraArmor(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightExtraArmor;

            case NameConsts.PRIEST :
                return PlayerStats.priestExtraArmor;

            case NameConsts.ARCHER :
                return PlayerStats.archerExtraArmor;

            default :
                System.out.println("Getting the extra armor of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said bonus stat of the given character.
     */
    public static float getUnitExtraSpeed(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightExtraSpeed;

            case NameConsts.PRIEST :
                return PlayerStats.priestExtraSpeed;

            case NameConsts.ARCHER :
                return PlayerStats.archerExtraSpeed;

            default :
                System.out.println("Getting the extra speed of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said bonus stat of the given character.
     */
    public static int getUnitExtraAttackCd(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightExtraAttackCd;

            case NameConsts.PRIEST :
                return PlayerStats.priestExtraAttackCd;

            case NameConsts.ARCHER :
                return PlayerStats.archerExtraAttackCd;

            default :
                System.out.println("Getting the extra attack_cd of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said bonus stat of the given character.
     */
    public static float getUnitExtraAttackRange(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightExtraAttackRange;

            case NameConsts.PRIEST :
                return PlayerStats.priestExtraAttackRange;

            case NameConsts.ARCHER :
                return PlayerStats.archerExtraAttackRange;

            default :
                System.out.println("Getting the extra attack_range of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * Sets the said bonus stat of the given character.
     */
    public static void setUnitExtraHp(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightExtraHp = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestExtraHp = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerExtraHp = value;
                return;

            default :
                System.out.println("Setting the extra hp of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said bonus stat of the given character.
     */
    public static void setUnitExtraDamage(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightExtraDamage = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestExtraDamage = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerExtraDamage = value;
                return;

            default :
                System.out.println("Setting the extra damage of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said bonus stat of the given character.
     */
    public static void setUnitExtraArmor(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightExtraArmor = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestExtraArmor = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerExtraArmor = value;
                return;

            default :
                System.out.println("Setting the extra armor of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said bonus stat of the given character.
     */
    public static void setUnitExtraSpeed(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightExtraSpeed = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestExtraSpeed = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerExtraSpeed = value;
                return;

            default :
                System.out.println("Setting the extra speed of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said bonus stat of the given character.
     */
    public static void setUnitExtraAttackCd(String character, int value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightExtraAttackCd = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestExtraAttackCd = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerExtraAttackCd = value;
                return;

            default :
                System.out.println("Setting the extra attack_cd of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said bonus stat of the given character.
     */
    public static void setUnitExtraAttackRange(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightExtraAttackRange = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestExtraAttackRange = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerExtraAttackRange = value;
                return;

            default :
                System.out.println("Setting the extra attack_range of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static float getUnitHp(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightHp;

            case NameConsts.PRIEST :
                return PlayerStats.priestHp;

            case NameConsts.ARCHER :
                return PlayerStats.archerHp;

            default :
                System.out.println("Getting the hp of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static float getUnitDamage(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightDamage;

            case NameConsts.PRIEST :
                return PlayerStats.priestDamage;

            case NameConsts.ARCHER :
                return PlayerStats.archerDamage;

            default :
                System.out.println("Getting the damage of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static float getUnitArmor(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightArmor;

            case NameConsts.PRIEST :
                return PlayerStats.priestArmor;

            case NameConsts.ARCHER :
                return PlayerStats.archerArmor;

            default :
                System.out.println("Getting the armor of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static float getUnitSpeed(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightSpeed;

            case NameConsts.PRIEST :
                return PlayerStats.priestSpeed;

            case NameConsts.ARCHER :
                return PlayerStats.archerSpeed;

            default :
                System.out.println("Getting the speed of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static int getUnitAttackCd(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightAttackCd;

            case NameConsts.PRIEST :
                return PlayerStats.priestAttackCd;

            case NameConsts.ARCHER :
                return PlayerStats.archerAttackCd;

            default :
                System.out.println("Getting the attack_cd of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static float getUnitAttackRange(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightAttackRange;

            case NameConsts.PRIEST :
                return PlayerStats.priestAttackRange;

            case NameConsts.ARCHER :
                return PlayerStats.archerAttackRange;

            default :
                System.out.println("Getting the attack_range of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static int getUnitAggroRange(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightAggroRange;

            case NameConsts.PRIEST :
                return PlayerStats.priestAggroRange;

            case NameConsts.ARCHER :
                return PlayerStats.archerAggroRange;

            default :
                System.out.println("Getting the aggro_range of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static int getUnitExp(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightExp;

            case NameConsts.PRIEST :
                return PlayerStats.priestExp;

            case NameConsts.ARCHER :
                return PlayerStats.archerExp;

            default :
                System.out.println("Getting the exp of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static int getUnitLvl(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return PlayerStats.knightLvl;

            case NameConsts.PRIEST :
                return PlayerStats.priestLvl;

            case NameConsts.ARCHER :
                return PlayerStats.archerLvl;

            default :
                System.out.println("Getting the level of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * @return The said stat of the given character.
     */
    public static float getUnitProjectileSpeed(String character){
        switch(character){
            case NameConsts.ARCHER :
                return PlayerStats.archerProjectileSpeed;

            default :
                System.out.println("Getting the projectile_speed of a character which is not included in the switch state : " + character);
                return 0;
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    private static void setUnitHp(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightHp = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestHp = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerHp = value;
                return;

            default :
                System.out.println("Setting the hp of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    private static void setUnitDamage(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightDamage = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestDamage = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerDamage = value;
                return;

            default :
                System.out.println("Setting the damage of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    public static void setUnitArmor(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightArmor = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestArmor = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerArmor = value;
                return;

            default :
                System.out.println("Setting the armor of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    public static void setUnitSpeed(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightSpeed = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestSpeed = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerSpeed = value;
                return;

            default :
                System.out.println("Setting the speed of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    private static void setUnitAttackCd(String character, int value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightAttackCd = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestAttackCd = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerAttackCd = value;
                return;

            default :
                System.out.println("Setting the attack_cd of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    private static void setUnitAttackRange(String character, float value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightAttackRange = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestAttackRange = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerAttackRange = value;
                return;

            default :
                System.out.println("Setting the attack_range of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    private static void setUnitAggroRange(String character, int value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightAggroRange = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestAggroRange = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerAggroRange = value;
                return;

            default :
                System.out.println("Setting the aggro_range of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    public static void setUnitExp(String character, int value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightExp = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestExp = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerExp = value;
                return;

            default :
                System.out.println("Setting the exp of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    public static void setUnitLvl(String character, int value){
        switch(character){
            case NameConsts.KNIGHT :
                PlayerStats.knightLvl = value;
                return;

            case NameConsts.PRIEST :
                PlayerStats.priestLvl = value;
                return;

            case NameConsts.ARCHER :
                PlayerStats.archerLvl = value;
                return;

            default :
                System.out.println("Setting the level of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Sets the said stat of the given character.
     */
    private static void setUnitProjectileSpeed(String character, float value){
        switch(character){
            case NameConsts.ARCHER :
                PlayerStats.archerProjectileSpeed = value;
                return;

            default :
                System.out.println("Setting the projectile_speed of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * @return A lock that controls the synchronization of all actions performed on the unlockedCharacters list.
     */
    public static Object getUnlockedCharactersLock() {
        return unlockedCharactersLock;
    }

    /**
     * @return An ArrayList of all the unlocked characters.
     */
    public static ArrayList<String> getUnlockedCharacters() {
        return unlockedCharacters;
    }

    /**
     * Levels up the unit.
     */
    public static void lvlUpUnit(String character) {
        switch(character){
            case NameConsts.KNIGHT :
                knightLvlUp();
                return;

            case NameConsts.PRIEST :
                priestLvlUp();
                return;

            case NameConsts.ARCHER :
                archerLvlUp();
                return;

            default :
                System.out.println("Leveling up a character which is not included in the switch state : " + character);
        }
    }

    /**
     * Updates the stats of the unit.
     */
    public static void updateUnitStats(String character) {
        switch(character){
            case NameConsts.KNIGHT :
                updateKnightStats();
                return;

            case NameConsts.PRIEST :
                updatePriestStats();
                return;

            case NameConsts.ARCHER :
                updateArcherStats();
                return;

            default :
                System.out.println("Updating the stats of a character which is not included in the switch state : " + character);
        }
    }

    /**
     * @return The portrait of the unit.
     */
    public static Texture getUnitPortrait(String character) {
        switch(character){
            case NameConsts.KNIGHT :
                return portraitKnight;

            case NameConsts.PRIEST :
                return portraitPriest;

            case NameConsts.ARCHER :
                return portraitArcher;

            default :
                System.out.println("Getting the portrait of a character which is not included in the switch state : " + character);
                return new Texture(TextureData.unknown, Consts.PORTRAIT_WIDTH, Consts.PORTRAIT_HEIGHT);
        }
    }

    /**
     * @return The animation of the unit.
     */
    public static Animation getUnitAnimation(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return new Animation(AnimationLoader.knightAttackRight, 120) {
                    @Override
                    public void initAnimation() {

                    }

                    @Override
                    public void halfWay() {

                    }

                    @Override
                    public void extraEffects() {

                    }

                    @Override
                    public void finished() {

                    }

                    @Override
                    public void onEnd() {

                    }

                    @Override
                    public void onCycleEnd() {

                    }
                };

            case NameConsts.PRIEST :
                return new Animation(AnimationLoader.priestAttackRight, 120) {
                    @Override
                    public void initAnimation() {

                    }

                    @Override
                    public void halfWay() {

                    }

                    @Override
                    public void extraEffects() {

                    }

                    @Override
                    public void finished() {

                    }

                    @Override
                    public void onEnd() {

                    }

                    @Override
                    public void onCycleEnd() {

                    }
                };

            case NameConsts.ARCHER :
                return new Animation(AnimationLoader.archerAttackRight, 120) {
                    @Override
                    public void initAnimation() {

                    }

                    @Override
                    public void halfWay() {

                    }

                    @Override
                    public void extraEffects() {

                    }

                    @Override
                    public void finished() {

                    }

                    @Override
                    public void onEnd() {

                    }

                    @Override
                    public void onCycleEnd() {

                    }
                };

            default :
                System.out.println("Getting the animation of a character which is not included in the switch state : " + character);
                return null;
        }
    }

    /**
     * @return The last_level_unlocked.
     */
    public static int getLastLevelUnlocked() {
        return lastLevelUnlocked;
    }

    /**
     * Sets the last_level_unlocked.
     */
    public static void setLastLevelUnlocked(int lastLevelUnlocked) {
        PlayerStats.lastLevelUnlocked = lastLevelUnlocked;
    }

    public static int getUnitPositionIndicator(String character){
        switch(character){
            case NameConsts.KNIGHT :
                return TextureData.color_blue;

            case NameConsts.PRIEST :
                return TextureData.color_blue;

            case NameConsts.ARCHER :
                return TextureData.color_yellow;

            default :
                System.out.println("Getting the position indicator of an unknown unit.");
                return TextureData.color_yellow;
        }
    }
}