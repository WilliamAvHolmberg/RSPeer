package com.nex.communication;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Stack;

import com.nex.communication.message.request.RequestAccountInfo;
import com.nex.task.NexTask;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.subclass.GameCanvas;
import org.rspeer.ui.Log;

import com.nex.communication.message.DisconnectMessage;
import com.nex.communication.message.NexMessage;
import com.nex.communication.message.request.RequestAccountInfo;
import com.nex.communication.message.request.RequestTask;
import com.nex.script.Nex;
import com.nex.script.handler.TaskHandler;

public class NexHelper implements Runnable {
	
	private String ip = "nexus.myftp.biz";
	private int port = 43594;
	
	
	public static long lastLog = System.currentTimeMillis();

	private static Stack<NexMessage> messageQueue;
	private static String myIP = null;
	private String respond = "none";

	private NexMessage nextRequest;
	private String mail;
	private String password;
	private String log;
	private String name;
	public boolean initialized = false;

	public static Stack<NexMessage> getQueue() {
		return messageQueue;
	}
	
	public static void pushMessage(NexMessage message) {
		boolean add = true;
		for(NexMessage mess : messageQueue) {
			if(mess.getClass() == message.getClass()) {
				add = false;
				return;
			}
		}
		if(add) {
			messageQueue.push(message);
		}
	}
	static boolean emptyNow = false;
	public static void sendAllMessages(){
		emptyNow = true;
	}
	public NexHelper(String username, String password) {
		this.mail = username;
		this.password = password;
		this.name = username.split("@")[0];
		messageQueue = new Stack<NexMessage>();
		pushMessage(new RequestAccountInfo());
	}

	@Override
	public void run() {
		Log.fine("started NexHelper 2.0 with selenium support");
		for(int retry = 0; retry < 200; retry++) {
			try {
				//int tmp_port = port + Random.nextInt(0, 3);
				//Log.fine("Connecting port " + tmp_port);
				Socket socket = new Socket(ip, port);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				Log.fine("CONNECTED!");
				System.out.println("SCRIPT CONNECTED TO NEX");//Used to tell Nexus Desktop the script has launched
				System.out.println(mail);
				for (int i = 0; i < 10; i++) {
					if (Game.isLoggedIn() || !messageQueue.empty()) {//If we have messages to send, lets send them. Especially if its a ban message
						break;
					}
					Thread.sleep(1000);
				}
				initialized = initializeContactToSocket(out, in);
				retry = 0;
				lastLog = System.currentTimeMillis();//Reset timeout
				whileShouldRun(out, in); // main loop, always run while script should be running
			} catch (Exception e) {
				e.printStackTrace();//These saved my life
				Log.severe(e);
				Log.info("FAILED TO INITIALIZE: LETS TRY AGAIN");
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!Nex.SHOULD_RUN) {
				if (!GameCanvas.isInputEnabled())
					System.exit(1);
				break;
			}
			Log.fine("TRYING TO RE ESTABLISH CONNECTION");
		}
	}
	

	private void whileShouldRun(PrintWriter out, BufferedReader in) throws IOException, InterruptedException {
		//if last log more than 3 minutes, dc
		Log.fine(secondsSinceLastLog());
		while ((Nex.SHOULD_RUN || (emptyNow && !messageQueue.isEmpty())) && secondsSinceLastLog() < 180) {
			logToServer(out, in);
			//	checkIfBanned(out, in);
			if (!messageQueue.isEmpty()) {
				handleMessageQueue(out, in);
				if(emptyNow) continue;
			}
			checkStuck();
			emptyNow = false;
			Thread.sleep(1000);
		}
		//Nex.SHOULD_RUN = false;
		
	}

	long loggedOutSince = 0;
	static long lastSetCurPos = 0;
	long posTimeout = 10 * 60 * 1000;
	Position lastPos;
	void checkStuck(){
		if (!Game.isLoggedIn()){
			if(loggedOutSince == 0) loggedOutSince = System.currentTimeMillis();
			if(System.currentTimeMillis() - loggedOutSince > (4 * 60 * 1000))
				NexHelper.pushMessage(new DisconnectMessage("Been logged out for too long"));
		} else {
			loggedOutSince = 0;
			Player player = Players.getLocal();
			Position pos = player.getPosition();
			if (lastPos == null || lastPos.distance(pos) > 3 || pos.distance(BankLocation.GRAND_EXCHANGE.getPosition()) < 15) {
				lastPos = pos;
				clearWatchdog();
			}
			if (lastSetCurPos != 0 && (System.currentTimeMillis() - lastSetCurPos) > posTimeout)
				System.exit(1);
		}
	}
	public static void clearWatchdog(){
		lastSetCurPos = System.currentTimeMillis();
	}

	private void handleMessageQueue(PrintWriter out, BufferedReader in) throws InterruptedException, IOException {
		nextRequest = messageQueue.pop();
		if (nextRequest != null) {
			Log.fine("REQUEST:" + nextRequest);
			nextRequest.execute(out, in);
		} else {
			logToServer(out, in);
		}

	}

	/*
	 * Initialize contact towards socket if connection fails, stop script
	 */
	private boolean initializeContactToSocket(PrintWriter out, BufferedReader in) throws IOException {
		out.println("script:1:" + getIP() + ":" + mail + ":" + password + ":"
				+ getName() + ":" + Worlds.getCurrent());
		respond = in.readLine();
		if (respond.contains("connected:1")) {
			if(respond.split(":")[2] != null) {
				Nex.USERNAME = respond.split(":")[2];
			}
			Log.fine("NexHelper has been initialized towards Nexus");
			return true;
		} else {
			Log.fine("Connection Towards Nexus failed");
			messageQueue.push(new DisconnectMessage("failed to initialize contact"));
			return false;
		}
	}
	
	public String getName() {
		if(Game.isLoggedIn())
			return Players.getLocal().getName();
		return "?";
	}
	
	

	private String getLog() {
		/**
		 * If task is null, return log:0
		 * 
		 * if task is not null and xp per hour > 100 ++ log:1 + xpPerHour if task is not
		 * null and money per hour > 100 ++ moneyPerHour if player is logged in and
		 * position not null, += position coordinates
		 */

		NexTask curTask = TaskHandler.getCurrentTask();
		if (curTask == null || curTask.getLog() == null) {
			return "log:0";
		}
		String respond = "task_log:1:" + TaskHandler.getCurrentTask().getLog();
		return respond;
	}

	/*
	 * Method to take care of every log
	 */
	private void logToServer(PrintWriter out, BufferedReader in) throws InterruptedException, IOException {
		if (System.currentTimeMillis() - lastLog > 10_000) { // only log every 5 sec
			logToServerNow(out, in);
		}
	}
	public void logToServerNow(PrintWriter out, BufferedReader in) throws InterruptedException, IOException {
		log = getLog();
		out.println(log);
		respond = in.readLine();
		Log.fine((respond));
		if (respond.contains("DISCONNECT"))
			NexHelper.pushMessage(new DisconnectMessage("Told to Disconnect"));
		lastLog = System.currentTimeMillis();
	}

	long lastAskedForTask = 0;
	public void getNewTask() {
		if(System.currentTimeMillis() - lastAskedForTask < 6000)
			return;
		lastAskedForTask = System.currentTimeMillis();
		pushMessage(new RequestTask("none"));
	}

	/*private void checkIfBanned(PrintWriter out, BufferedReader in) throws IOException {
		if (!Game.isLoggedIn() && isDisabledMessageVisible()) {
			messageQueue.push(new BannedMessage("Player is banned"));
		}
	}*/



	public static String getIP() {
		if(myIP != null) {
			return myIP;
		}
		try {
			URL url = new URL("http://checkip.amazonaws.com/");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			myIP = br.readLine();
			return myIP;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "not_found";

	}
	
	public static long secondsSinceLastLog() {
		return (System.currentTimeMillis() - NexHelper.lastLog)/1000;
	}

}
