package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import org.rspeer.runetek.api.component.tab.EquipmentSlot;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.ui.Log;

import com.nex.communication.message.NexMessage;
import com.nex.handler.gear.Gear;
import com.nex.script.Nex;
import com.nex.script.WebBank;
import com.nex.script.handler.TaskHandler;
import com.nex.script.items.RSItem;
import com.nex.task.SkillTask;
import com.nex.task.fishing.*;
import com.nex.task.fishing.FishingTask;

import com.nex.task.woodcutting.WoodcuttingTask;

public class FishingRespond extends TaskRespond {

    public FishingRespond(String respond) {
        super(respond);
    }

    @Override
    public void execute(PrintWriter out, BufferedReader in) throws IOException {
        if(respond != null) {
        }else {
        }
        String[] parsed = respond.split(":");
        String currentTaskID = parsed[3];
        String parsedBankArea = parsed[4];
        String parsedActionArea = parsed[5];
        String parsedFishName = parsed[6];
        String parsedBreakCondition = parsed[7];
        String breakAfter = parsed[8];
        String parsedLevelGoal = parsed[9];
        ArrayList<String> listOfParsedGear = new ArrayList<String>();
        String parsedHelm = parsed[10];
        String parsedCape = parsed[11];
        String parsedAmulet = parsed[12];
        String parsedWeapon = parsed[13];
        String parsedChest = parsed[14];
        String parsedShield = parsed[15];
        String parsedLegs = parsed[16];
        String parsedGloves = parsed[17];
        String parsedBoots = parsed[18];
        String parsedRing = parsed[19];
        String parsedAmmo = parsed[20];
        listOfParsedGear.add(parsedHelm);
        listOfParsedGear.add(parsedCape);
        listOfParsedGear.add(parsedAmulet);
        listOfParsedGear.add(parsedWeapon);
        listOfParsedGear.add(parsedChest);
        listOfParsedGear.add(parsedShield);
        listOfParsedGear.add(parsedLegs);
        listOfParsedGear.add(parsedGloves);
        listOfParsedGear.add(parsedBoots);
        listOfParsedGear.add(parsedRing);
        listOfParsedGear.add(parsedAmmo);

        Gear gear = getGear(parsed, listOfParsedGear);
        Area actionArea = WebBank.parseCoordinates(parsedActionArea);
        Area bankArea = null;
        if (!parsedBankArea.equals("none")) {
            bankArea = WebBank.parseCoordinates(parsedBankArea);
        }

        SkillTask newTask = new FishingTask(actionArea, bankArea, parsedFishName);
        newTask.setGear(gear);
        newTask.setTaskID(currentTaskID);
        setBreakConditions(newTask, parsedBreakCondition, breakAfter, parsedLevelGoal);
        TaskHandler.addPrioritizedTask(newTask);
    }


}
