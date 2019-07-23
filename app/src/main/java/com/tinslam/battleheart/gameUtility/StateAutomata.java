package com.tinslam.battleheart.gameUtility;

import android.graphics.Rect;

import com.tinslam.battleheart.entities.projectiles.PowerShotArrow;
import com.tinslam.battleheart.entities.units.PCs.Archer;
import com.tinslam.battleheart.entities.units.PCs.Pc;
import com.tinslam.battleheart.entities.units.Unit;
import com.tinslam.battleheart.spells.Spell;
import com.tinslam.battleheart.states.BattleState;
import com.tinslam.battleheart.states.ReplayState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class StateAutomata{
    private static HashMap<String, Object> map = new HashMap<>();
    private static ArrayList<StateAutomata> states = new ArrayList<>();
    private static int currentStateIndex = 0;
    private ArrayList<String> events = new ArrayList<>();

    public StateAutomata(ArrayList<String> events){
        this.events = events;
    }

    public static void clear() {
        map.clear();
        states.clear();
        currentStateIndex = 0;
    }

    private void triggerEvents(){
        try{
            for(String event : events){
                if(event.startsWith("spawn_unit")){
                    String[] splits = event.split(" ");
                    Class<?> unitClass = Class.forName("com.tinslam.battleheart.entities.units." + splits[1]);
                    Constructor constructor = unitClass.getConstructor(float.class, float.class);
                    Unit unit = (Unit) constructor.newInstance(Float.parseFloat(splits[3]), Float.parseFloat(splits[4]));
                    map.remove(splits[2]);
                    map.put(splits[2], unit);
                    String[] hpSplits = splits[5].split("/");
                    unit.setMaxHp(Float.parseFloat(hpSplits[1]));
                    unit.setHp(Float.parseFloat(hpSplits[0]));
                }else if(event.startsWith("spawn_boss")){
                    String[] splits = event.split(" ");
                    Class<?> unitClass = Class.forName("com.tinslam.battleheart.entities.units.NPCs.enemyNPCs.bosses." + splits[1]);
                    String[] rectInfo = splits[6].split("-");
                    Rect rect = new Rect(Integer.parseInt(rectInfo[0]),
                            Integer.parseInt(rectInfo[1]),
                            Integer.parseInt(rectInfo[2]),
                            Integer.parseInt(rectInfo[3]));
                    Constructor constructor = unitClass.getConstructor(float.class, float.class, Rect.class, int.class);
                    Unit unit = (Unit) constructor.newInstance(Float.parseFloat(splits[3]), Float.parseFloat(splits[4]), rect, Integer.parseInt(splits[7]));
                    map.remove(splits[2]);
                    map.put(splits[2], unit);
                    String[] hpSplits = splits[5].split("/");
                    unit.setMaxHp(Float.parseFloat(hpSplits[1]));
                    unit.setHp(Float.parseFloat(hpSplits[0]));
                }else if(event.startsWith("call_method")){
                    String[] splits = event.split(" ");
                    if(splits[2].equalsIgnoreCase("damageReplay")){
                        Unit object = (Unit) map.get(splits[1]);
                        Object damage = Float.parseFloat(splits[3]);
                        Object attacker = map.get(splits[4]);
                        Method method = object.getClass().getMethod(splits[2], float.class, Unit.class);
                        method.invoke(object, damage, attacker);
                    }
                }else if(event.startsWith("command")){
                    String[] splits = event.split(" ");
                    if(splits[2].equalsIgnoreCase("move")){
                        Unit object = (Unit) map.get(splits[1]);
                        Object x = Float.parseFloat(splits[3]);
                        Object y = Float.parseFloat(splits[4]);
                        BattleState.getBattleState().queueEvent(new Object[] {"move", object, x, y});
                    }else if(splits[2].equalsIgnoreCase("attack")){
                        Unit object = (Unit) map.get(splits[1]);
                        Object paramObject = map.get(splits[3]);
                        BattleState.getBattleState().queueEvent(new Object[] {"attack", object, paramObject});
                    }else if(splits[1].equalsIgnoreCase("setSelectedPc")){
                        BattleState.getBattleState().setSelectedPc((Pc) map.get(splits[2]));
                    }
                }else if(event.startsWith("transition")){
                    String[] splits = event.split(" ");
                    ReplayState.getReplayState().setTransitionFrames(Integer.parseInt(splits[1]));
                }else if(event.startsWith("spawn_spells")){
                    String[] splits = event.split(" ");
                    ReplayState.getReplayState().getSpells().clear();
                    for(int i = 2; i < 2 + 3 * Integer.parseInt(splits[1]); i += 3){
                        Unit caster = (Unit) map.get(splits[i + 2]);
                        Spell spell = (Spell) Class.forName("com.tinslam.battleheart.spells." + splits[i]).getConstructor(Unit.class).newInstance(caster);
                        map.remove(splits[i + 1]);
                        map.put(splits[i + 1], spell);
                        ReplayState.getReplayState().getSpells().add(spell);
                    }
                    ReplayState.getReplayState().loadSpells();
                }else if(event.startsWith("click_button")){
                    String[] splits = event.split(" ");
                    ReplayState.getReplayState().getSpellButtons().get(ReplayState.getReplayState().getSpells().indexOf(map.get(splits[1]))).onUp();
                }else if(event.startsWith("cast_powershot")){
                    String[] splits = event.split(" ");
                    Spell spell = ((Spell) map.get(splits[1]));
                    spell.cast();
                    new PowerShotArrow((Archer) spell.getCaster(), Float.parseFloat(splits[2]), Float.parseFloat(splits[3]));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void setStates(ArrayList<StateAutomata> states){
        StateAutomata.states = states;
    }

    public static void transition() {
        states.get(currentStateIndex).triggerEvents();
        currentStateIndex++;
    }
}
