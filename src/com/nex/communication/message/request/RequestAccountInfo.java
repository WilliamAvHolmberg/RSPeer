package com.nex.communication.message.request;

import com.nex.script.Nex;
import com.nex.script.handler.RandomHandler;
import com.nex.task.quests.events.EnableFixedModeEvent;
import com.nex.task.quests.tutorial.TutorialIsland;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RequestAccountInfo extends NexRequest {

    public static String account_id;
    public static String account_type;
    public static String schema_name;
    public static String computer_name;
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
        String[] parsedRespond = respond.split(":");

        account_id = parsedRespond[1];
        account_type = parsedRespond[2];
        schema_name = parsedRespond[3];
        computer_name = parsedRespond[4];
        Log.fine("Account ID: " + account_id);
        Log.fine("Account Type: " + account_type);
        Log.fine("Account Schema: " + schema_name);
        Log.fine("Computer Name: " + computer_name);
        try {
            created_at = ZonedDateTime.parse(parsedRespond[5].replace('.', ':'),
                    DateTimeFormatter.RFC_1123_DATE_TIME);
            long hoursAlive = ChronoUnit.MINUTES.between(created_at, ZonedDateTime.now());
            Log.fine("We are " + Math.round(hoursAlive / 60.0f) + " hours old");
        }catch (Exception ex){
            Log.severe(ex);
        }

        if (account_type == "MULE"){
            Nex.MULE_THRESHOLD = 4_000_000;
        }

        if(computer_name.equals("SERVER")){
            RandomHandler.ENABLED = true;
            EnableFixedModeEvent.EXIT_ON_CREATE = false;
//            java.util.Random r = new java.util.Random(Nex.USERNAME.hashCode());
//            TutorialIsland.DO_NOOB_FIGHTING = r.nextDouble() > 0.5;
//            if(Inventory.getCount(true, 995) > 5000)
//                TutorialIsland.DO_NOOB_FIGHTING = false;
        }
        else {
            // Williams settings here :)
            RandomHandler.ENABLED = false;
            TutorialIsland.DO_NOOB_FIGHTING = false;
        }
    }

}
