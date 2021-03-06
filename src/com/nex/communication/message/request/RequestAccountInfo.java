package com.nex.communication.message.request;

import com.nex.script.Nex;
import com.nex.script.handler.RandomHandler;
import com.nex.task.quests.events.EnableFixedModeEvent;
import com.nex.task.quests.tutorial.TutorialIsland;
import com.nex.utils.json.JsonObject;

import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.ui.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RequestAccountInfo extends NexRequest {

    public static int account_id;
    public static String account_type;
    public static String schema_name;
    public static String computer_name;
    public static boolean is_member;
    public static ZonedDateTime created_at;

    public RequestAccountInfo() {
        super("account_info:1");
    }

    @Override
    public void execute(PrintWriter out, BufferedReader in) throws IOException {
        String request = "account_info:1";
        println(out, request);
        String respond = in.readLine();
        Log.fine("got account info - " + respond);
        handleRespond(respond);
    }

    static long timeAskedToDC = 0;
    static String lastTask;
    private void handleRespond(String respond) {
    	if(respond != null && respond.length() > 2) {
    	JsonObject jsonRespond = JsonObject.readFrom(respond);
        account_id = jsonRespond.get("account_id").asInt();
        account_type = jsonRespond.get("account_type").asString();
        schema_name = jsonRespond.get("schema_name").asString();
        computer_name = jsonRespond.get("computer_name").asString();
        String parsed_created_at = jsonRespond.get("created_at").asString();
        is_member = jsonRespond.get("member").asBoolean();
        Log.fine("Account ID: " + account_id);
        Log.fine("Account Type: " + account_type);
        Log.fine("Account Schema: " + schema_name);
        Log.fine("Computer Name: " + computer_name);
        try {
            created_at = ZonedDateTime.parse(parsed_created_at.replace('.', ':'),
                    DateTimeFormatter.RFC_1123_DATE_TIME);
            long hoursAlive = ChronoUnit.MINUTES.between(created_at, ZonedDateTime.now());
            Log.fine("We are " + Math.round(hoursAlive / 60.0f) + " hours old");
        }catch (Exception ex){
            Log.severe(ex);
        }

        boolean isMule = "MULE".equalsIgnoreCase(account_type);
        boolean isSlave = "SLAVE".equalsIgnoreCase(account_type);
        if (isMule){
            Nex.MULE_THRESHOLD = 4_000_000;
            Nex.MONEY_NEEDED = 1_500_000;
        }

        if("SERVER".equalsIgnoreCase(computer_name) || "SERVER2".equalsIgnoreCase(computer_name)){
            RandomHandler.ENABLED = true;
            EnableFixedModeEvent.EXIT_ON_CREATE = false;
            java.util.Random r = new java.util.Random(Nex.USERNAME.length());
            Log.fine("Time since big ban " + Nex.timeSinceBanWave());
            TutorialIsland.DO_NOOB_FIGHTING = isSlave && Nex.isHoursAfterBan();
            if(Inventory.getCount(true, 995) > 100 || Skills.getLevel(Skill.WOODCUTTING) > 1)
                TutorialIsland.DO_NOOB_FIGHTING = false;
        }
        else {
            // Williams settings here :)
            RandomHandler.ENABLED = false;
            TutorialIsland.DO_NOOB_FIGHTING = false;
        }
        if(is_member){
        	Log.fine("IS MEMBER");
        	Nex.IS_MEMBER = true;
        	Nex.MULE_THRESHOLD = 100000000; //"DEBUG
        }
    	}
    }

}
