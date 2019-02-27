package com.nex.communication.message.request;

import com.nex.communication.NexHelper;
import com.nex.communication.message.DisconnectMessage;
import com.nex.communication.message.respond.*;
import com.nex.script.Nex;
import com.nex.script.Quest;
import com.nex.task.quests.tutorial.TutorialIsland;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
        out.println(request);
        String respond = in.readLine();
        Log.fine("got account info from account_info:" + respond);
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
        try {
            created_at = ZonedDateTime.parse(parsedRespond[5].replace('.', ':'),
                    DateTimeFormatter.RFC_1123_DATE_TIME);
        }catch (Exception ex){
            Log.severe(ex);
        }

        if(computer_name == "SERVER"){
            java.util.Random r = new java.util.Random(Nex.USERNAME.hashCode());
            TutorialIsland.DO_NOOB_FIGHTING = r.nextDouble() > 0.5;
        }
    }

}
