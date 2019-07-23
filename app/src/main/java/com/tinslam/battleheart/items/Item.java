package com.tinslam.battleheart.items;

import android.graphics.Bitmap;

import com.tinslam.battleheart.gameUtility.PlayerStats;
import com.tinslam.battleheart.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * A class that holds all the items.
 */
public abstract class Item{
    private String carrier = "";
    protected String name = "Unknown item";
    protected String description = "No description available.";
    protected ArrayList<String> properties = new ArrayList<>();
    protected ArrayList<String> characters = new ArrayList<>();
    String type = "other";
    protected int image;
    protected int price = Integer.MAX_VALUE;

    protected float hp = 0;
    protected float damage = 0;
    protected float armor = 0;
    protected float speed = 0;
    protected int attackCd = 0;
    protected float attackRange = 0;

    private int attackCdDeficit = 0;

    /**
     * Constructor.
     * @param carrier Enter "" if no carrier.
     */
    public Item(String carrier){
        this.carrier = carrier;
    }

    /**
     * Adds the stats to the carrier's bonus stats.
     */
    private void addStats(){
        attackCdDeficit = (int) (-PlayerStats.getUnitAttackCd(carrier) * (1 - 100f / (100 + attackCd)));

        PlayerStats.setUnitExtraHp(carrier, PlayerStats.getUnitExtraHp(carrier) + hp);
        PlayerStats.setUnitExtraDamage(carrier, PlayerStats.getUnitExtraDamage(carrier) + damage);
        PlayerStats.setUnitExtraArmor(carrier, PlayerStats.getUnitExtraArmor(carrier) + armor);
        PlayerStats.setUnitExtraSpeed(carrier, PlayerStats.getUnitExtraSpeed(carrier) + speed);
        PlayerStats.setUnitExtraAttackCd(carrier, PlayerStats.getUnitExtraAttackCd(carrier) + attackCdDeficit);
        PlayerStats.setUnitExtraAttackRange(carrier, PlayerStats.getUnitExtraAttackRange(carrier) + attackRange);
    }

    /**
     * Removes the stats from the carrier's bonus stats.
     */
    private void removeStats(){
        PlayerStats.setUnitExtraHp(carrier, PlayerStats.getUnitExtraHp(carrier) - hp);
        PlayerStats.setUnitExtraDamage(carrier, PlayerStats.getUnitExtraDamage(carrier) - damage);
        PlayerStats.setUnitExtraArmor(carrier, PlayerStats.getUnitExtraArmor(carrier) - armor);
        PlayerStats.setUnitExtraSpeed(carrier, PlayerStats.getUnitExtraSpeed(carrier) - speed);
        PlayerStats.setUnitExtraAttackCd(carrier, PlayerStats.getUnitExtraAttackCd(carrier) - attackCdDeficit);
        PlayerStats.setUnitExtraAttackRange(carrier, PlayerStats.getUnitExtraAttackRange(carrier) - attackRange);
    }

    /**
     * Equips the item on the carrier specified. Will unequip from the last owner first.
     */
    public void equip(String carrier){
        if(!Utils.isCharacter(carrier)){
            System.out.println("Character name not valid to equip item on.");
            return;
        }
        removeStats();
        this.carrier = carrier;
        addStats();
        PlayerStats.updateItems();
    }

    /**
     * Unquips the item from the current owner.
     */
    public void unequip(){
        removeStats();
        carrier = "";
        PlayerStats.updateItems();
    }

    /**
     * Buys the item.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean buy(){
        if(PlayerStats.gold >= price){
            try{
                Class itemClass = this.getClass();
                @SuppressWarnings("unchecked") Constructor constructor = itemClass.getConstructor(String.class);
                Item item = (Item) constructor.newInstance((Object) "");
                PlayerStats.gold -= price;
                PlayerStats.items.add(item);
                PlayerStats.updateAccountStats();
                PlayerStats.updateItems();
            }catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * Sells the item.
     */
    public void sell(){
        PlayerStats.gold += getSellPrice();
        PlayerStats.items.remove(this);
        PlayerStats.updateAccountStats();
        PlayerStats.updateItems();
    }

    /**
     * Checks if the item is equipped on the said character.
     */
    public boolean isEquipped(String selectedCharacter){
        return carrier.equalsIgnoreCase(selectedCharacter);
    }

    /**
     * @return The sell price of an item.
     */
    public int getSellPrice(){
        return getPrice() / 10;
    }

    /**
     * @return The body type of the item. (Helmet, armor, boots, ...)
     */
    public String getType() {
        return type;
    }

    /**
     * @return The name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The description of the item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return An ArrayList of all the stats it gives.
     */
    public ArrayList<String> getProperties() {
        return properties;
    }

    /**
     * @return The image of the item.
     */
    public int getImage(){
        return image;
    }

    /**
     * @return An ArrayList of all compatible characters with the item.
     */
    public ArrayList<String> getCharacters(){
        return characters;
    }

    /**
     * @return The price of the item.
     */
    public int getPrice(){
        return price;
    }

    /**
     * @return The current carrier of the item.
     */
    public String getCarrier(){
        return carrier;
    }
}