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
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import com.nex.communication.message.DisconnectMessage;
import com.nex.communication.message.NexMessage;
import com.nex.communication.message.request.RequestTask;
import com.nex.script.Nex;
import com.nex.script.handler.TaskHandler;

public class NexHelper implements Runnable {
	// private String ip = "192.168.10.127";
	//private String ip = "oxnetserver.ddns.net";
	private String ip = "oxnetdebug.ddns.net";
	//private String ip = "nexus.no-ip.org";
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
		for(int retry = 0; retry < 3; retry++) {
			try {
				Socket socket = new Socket(ip, port);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				for (int i = 0; i < 10; i++) {
					if (Game.isLoggedIn() || !messageQueue.empty()) {//If we have messages to send, lets send them. Especially if its a ban message
						break;
					}
					Thread.sleep(1000);
				}
				retry = 0;
				initialized = initializeContactToSocket(out, in);
				whileShouldRun(out, in); // main loop, always run while script should be running

			} catch (Exception e) {
				e.printStackTrace();//These saved my life
				Log.info("FAILED TO INITIALIZE: LETS TRY AGAIN");
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!Nex.SHOULD_RUN) {
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
			emptyNow = false;
			Thread.sleep(1000);
		}
		//Nex.SHOULD_RUN = false;
		
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

		if (TaskHandler.getCurrentTask() == null || TaskHandler.getCurrentTask().getLog() == null) {
			return "log:0";
		}
		String respond = "task_log:1:" + TaskHandler.getCurrentTask().getLog();
		return respond;
	}

	/*
	 * Method to take care of every log
	 */
	private void logToServer(PrintWriter out, BufferedReader in) throws InterruptedException, IOException {
		if (System.currentTimeMillis() - lastLog > 5000) { // only log every 5 sec
			logToServerNow(out, in);
		}
	}
	public void logToServerNow(PrintWriter out, BufferedReader in) throws InterruptedException, IOException {
		log = getLog();
		out.println(log);
		respond = in.readLine();
		Log.fine((respond));
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
			return myIP.toString();
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
