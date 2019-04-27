package com.nex.script;

import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;

import com.nex.communication.message.DisconnectMessage;
import com.nex.communication.message.LockedMessage;
import com.nex.communication.message.request.MuleRequest;
import com.nex.communication.message.request.RequestAccountInfo;
import com.nex.communication.message.respond.MuleRespond;
import com.nex.script.handler.RandomHandler;
import com.nex.task.IHandlerTask;
import com.nex.task.boredmule.BoredMuleTask;
import com.nex.task.mule.DepositToPlayerTask;
import com.nex.task.mule.PrepareForMuleDepositTask;
import com.nex.task.mule.WithdrawFromPlayerTask;

import org.rspeer.RSPeer;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Login;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Mouse;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.LoginMessageListener;
import org.rspeer.runetek.event.listeners.LoginResponseListener;
import org.rspeer.runetek.event.listeners.ObjectSpawnListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.LoginMessageEvent;
import org.rspeer.runetek.event.types.LoginResponseEvent;
import org.rspeer.runetek.event.types.LoginResponseEvent.Response;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.providers.RSGrandExchangeOffer.Type;
import org.rspeer.runetek.providers.RSProvider;
import org.rspeer.runetek.providers.RSWorld;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.events.LoginScreen;
import org.rspeer.script.task.Task;
import org.rspeer.script.task.TaskScript;
import org.rspeer.ui.Log;
import org.slf4j.event.LoggingEvent;

import com.nex.communication.NexHelper;
import com.nex.communication.message.BannedMessage;
import com.nex.communication.message.LockedMessage;
import com.nex.handler.gear.Gear;
import com.nex.handler.gear.GearHandler;
import com.nex.handler.gear.GearItem;
import com.nex.script.banking.BankEvent;
import com.nex.script.banking.BankHandler;
import com.nex.script.grandexchange.BuyItemEvent;
import com.nex.script.grandexchange.BuyItemHandler;
import com.nex.script.grandexchange.SellItemEvent;
import com.nex.script.grandexchange.SellItemHandler;
import com.nex.script.handler.TaskHandler;
import com.nex.script.items.GEItem;
import com.nex.script.items.RSItem;
import com.nex.task.NexTask;
import com.nex.task.SkillTask;
import com.nex.task.actions.mule.CheckIfWeShallSellItems;
import com.nex.task.mining.MiningTask;
import com.nex.task.mule.Mule;
import com.nex.task.quests.CooksAssistantQuest;
import com.nex.task.quests.GoblinDiplomacyQuest;
import com.nex.task.quests.RomeoAndJulietQuest;
import com.nex.task.quests.tutorial.TutorialIsland;
import com.nex.task.woodcutting.WoodcuttingTask;

@ScriptMeta(desc = "Nex 2.5 - DEBUG", developer = "William", name = "nex")
public class Nex extends Script
		implements RenderListener, ChatMessageListener, ObjectSpawnListener, LoginResponseListener {

	public static boolean SHOULD_RUN = true;
	public static String USERNAME;
	public static int MULE_THRESHOLD = 50000;
	public static int MONEY_NEEDED = 0;
	public static int MIN_WITHDRAW = 10000;
	NexHelper nexHelper;
	boolean initialized = false;

	static float banWaveTime = 7.5f;
	public static boolean approachingBan(){
		float timeSinceBanWave = timeSinceBanWave();
		return (timeSinceBanWave > 23.25f && timeSinceBanWave < 0.16f);
	}
	public static boolean isHoursAfterBan(){
		float timeSinceBanWave = timeSinceBanWave();
		return timeSinceBanWave < 5;
	}
	static long lastCheckedTimeSinceBan = 0;
	static float lastTimeSinceBan = 0;
	public static float timeSinceBanWave(){
		if(System.currentTimeMillis() - lastCheckedTimeSinceBan < 30_000)
			return lastTimeSinceBan;
		SimpleDateFormat gmtDateFormatHH = new SimpleDateFormat("HH");
		SimpleDateFormat gmtDateFormatmm = new SimpleDateFormat("mm");
		gmtDateFormatHH.setTimeZone(TimeZone.getTimeZone("GMT"));
		gmtDateFormatmm.setTimeZone(TimeZone.getTimeZone("GMT"));
		int currentHourGMT = Integer.parseInt(gmtDateFormatHH.format(new Date()));
		float currentMinGMT = Integer.parseInt(gmtDateFormatmm.format(new Date())) / 60.0f;
		float time = currentHourGMT + currentMinGMT;
		if (currentHourGMT < banWaveTime) {
			return lastTimeSinceBan = currentHourGMT + (24 - banWaveTime);
		}
		return lastTimeSinceBan = currentHourGMT - banWaveTime;
	}

	public static int MULE_THRESHOLD_NOW(){
		int muleThreshold = Nex.MULE_THRESHOLD;
		if(approachingBan())
			muleThreshold *= 0.33;
		return muleThreshold;
	}

	Area barbMine = Area.rectangular(3077, 3424, 3086, 3416);
	public static int failedWalk = 0;
	public static boolean IS_MEMBER = false;
	@Override
	public void onStart() {
		nexHelper = new NexHelper(getAccount().getUsername(), getAccount().getPassword());
		
		Thread newThread = new Thread(nexHelper);
		newThread.start();
		this.removeBlockingEvent(LoginScreen.class);//As suggested by Spencer
		super.onStart();
		TaskHandler.addPrioritizedTask(new TutorialIsland());
		//TaskHandler.addPrioritizedTask(new RomeoAndJulietQuest());
		Time.sleep(1900, 3000);
		
//		TaskHandler.addPrioritizedTask(new MiningTask(barbMine, null, new Integer[] {7486,7485}, new RSItem("Bronze pickaxe", 1265)));
	}

	@Override
	public int loop() {
		if(failedWalk  >= 40 || !SHOULD_RUN || NexHelper.secondsSinceLastLog() > 120) {
			Log.fine("lets quit");
			System.exit(1);
		}
		//Log.fine("Nex Loop");
		if (loggedIn()) {
			if (taskIsCompleted()) {
				TaskHandler.removeTask();
			}else if(shouldDoHandler()) {
				//command query separation.. none for me. Fix this in a better way. for now. Handle like this
			}
			else if (TaskHandler.getCurrentTask() == null) {
				getTask();
			} else if (RandomHandler.handleRandom()) {
				return Random.nextInt(100, 500);
			} else {
				return TaskHandler.getCurrentTask().loop();
			}
		} else {
			login();
		}
		return 600;
	}



	BoredMuleTask boredMuleTask = null;
	private boolean shouldDoHandler() {
//		IHandlerTask activeHandler = TaskHandler.getLatesthandler();
//		if(activeHandler != null) {
//			activeHandler.execute();
//			return true;
//		}
//		else if(!GearHandler.itemsToEquip.isEmpty()) {
//			GearHandler.execute();
//			return true;
//		}
//		ArrayList<IHandlerTask> activeTasks = new ArrayList<>();
		BankEvent depositEvent = BankHandler.getDepositEvent();
		BankEvent withdrawEvent = BankHandler.getWithdrawEvent();
		BuyItemEvent buyItemEvent = BuyItemHandler.getBuyItemEvent();
		SellItemEvent sellItemEvent = SellItemHandler.getSellItemEvent();
		if (sellItemEvent != null) {
			SellItemHandler.execute(sellItemEvent);
			return true;
		}else if (TaskHandler.getCurrentTask() != null && TaskHandler.getCurrentTask() instanceof WithdrawFromPlayerTask) {
			TaskHandler.getCurrentTask().loop();
			return true;
		} else if (depositEvent != null) {
			BankHandler.executeEvent(depositEvent);
			return true;
		} else if (buyItemEvent != null) {
			BuyItemHandler.execute(buyItemEvent);
			return true;
		} else if (withdrawEvent != null) {
			BankHandler.executeEvent(withdrawEvent);
			return true;
		} else if(!GearHandler.itemsToEquip.isEmpty()) {
			GearHandler.execute();
			return true;
		}
//		Log.fine("No handler tasks");
//		int coins = Inventory.getCount(true, 995);
//		if("MULE".equals(RequestAccountInfo.account_type) && (coins > 100000 || boredMuleTask != null) && TaskHandler.available_tasks.isEmpty()) {
//			Log.fine("Idle Mule Task");
//			if (boredMuleTask == null) boredMuleTask = new BoredMuleTask();
//			boredMuleTask.execute();
//		}
		return false;
	}
	private boolean longWaitForTask(){
		return boredMuleTask == null;
	}
	private boolean taskIsCompleted() {
		return TaskHandler.getCurrentTask() != null && TaskHandler.getCurrentTask().isFinished();
	}

	private void getTask() {
		if(!loggedIn()) {
			Log.fine("wait til we are logged in before getting task");
			Time.sleep(1000);
		}else if(!TutorialIsland.isTutorialIslandCompleted()) {
			TaskHandler.addPrioritizedTask(new TutorialIsland());
		}else if (TaskHandler.available_tasks.isEmpty()) {
			nexHelper.getNewTask();
			if (longWaitForTask())
				Time.sleepUntil(() -> TaskHandler.getCurrentTask() != null ||!TaskHandler.available_tasks.isEmpty(), 60000);
		} else {
			TaskHandler.popTask();
		}
	}

	private void login() {
		Log.fine("Is not logged in");
		Log.fine("lets login with " + getAccount().getUsername() + ":" + getAccount().getPassword());
		Login.enterCredentials(getAccount().getUsername(), getAccount().getPassword());
		Mouse.click(279, 301);
	}

	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("IP:" + NexHelper.getIP(), 200,50);
		g.drawString("LAST LOG:" + NexHelper.secondsSinceLastLog(), 200,75);
//		g.drawString("BREAK AFTER 40", 200,100);
		int y = 200;
		NexTask curTask = TaskHandler.getCurrentTask();
		if (curTask != null) {
			curTask.notify(event);
			event.getSource().drawString("CURRENT_TASK:" + curTask.getClass().getSimpleName(), 100, 300);
			Gear gear = curTask.getGear();
			if(gear != null && !gear.gear.isEmpty()) {
				event.getSource().drawString("Not empty", 10, 10);
				int yz = 35;
				for(GearItem gearItem : gear.gear.values()) {
					if(gearItem != null) {
					event.getSource().drawString(gearItem.getItem().getName() + ":" + gearItem.getSlot(), 10, yz);
					yz+=25;
					}
				}
			}
		}
		g.drawString("next: " + CheckIfWeShallSellItems.getTimeTilNextCheckInMinutes(), 350, 100);
		
	}

	@Override
	public void notify(ChatMessageEvent event) {
		if (TaskHandler.getCurrentTask() != null) {
			TaskHandler.getCurrentTask().notify(event);
		}
	}

	@Override
	public void notify(ObjectSpawnEvent event) {
		if (TaskHandler.getCurrentTask() != null) {
			TaskHandler.getCurrentTask().notify(event);
		}
	}

	public static boolean loggedIn() {
		return Game.isLoggedIn() && Game.getState() != Game.STATE_LOGGING_IN;
	}

	@Override
	public void onStop() {
		SHOULD_RUN = false;
		super.onStop();
	}

	@Override
	public void notify(LoginResponseEvent arg0) {
		Log.fine(arg0.getResponse());
		switch (arg0.getResponse()) {
		case ACCOUNT_DISABLED:
		case INVALID_CREDENTIALS:
			NexHelper.pushMessage(new BannedMessage("We are banned"));
			NexHelper.sendAllMessages();
			break;
		case ACCOUNT_INACCESSIBLE:
		case ACCOUNT_NOT_LOGGED_OUT:
		case BAD_SESSION_ID:

		case COMPUTER_ADDRESS_BLOCKED:

		case CONNECTION_TIMED_OUT:

		case COULD_NOT_COMPLETE:
		case ENTER_AUTH:
		case ERROR_CONNECTING:
		case ERROR_LOADING_PROFILE:
		case INCORRECT_AUTH_CODE:
		case INVALID_LOGIN_SERVER:
		case LOGIN_LIMIT:
		case LOGIN_SERVER_OFFLINE:
		case MALFORMED_LOGIN_PACKET:
		case MEMBERSHIP_REQUIRED:
		case MEMBERS_ONLY_AREA:
		case NO_DISPLAY_NAME_SET:
		case NO_SERVER_REPLY:
		case NO_SERVER_RESPONSE:
		case RUNESCAPE_UPDATE:
		case SERVER_BEING_UPDATED:
		case SERVICE_UNAVAILABLE:
		case TOO_MANY_ATTEMPTS:
		case UNEXPECTED_SERVER_RESPONSE:
		case UNSUCCESSFUL_ACCOUNT_LOGIN_ATTEMPT:
		case VOTE_REQUIRED:
		case WORLD_CLOSED_BETA:
		case WORLD_FULL:
			NexHelper.pushMessage(new DisconnectMessage(arg0.getResponse().toString()));
			NexHelper.sendAllMessages();
			Time.sleep(1500);//Give us a quick glimpse of the result before shutting down
			System.exit(1);
			break;
		case ACCOUNT_LOCKED:
		case ACCOUNT_STOLEN:
			NexHelper.pushMessage(new LockedMessage("We are locked"));
			NexHelper.sendAllMessages();
			Time.sleep(1500);//Give us a quick glimpse of the result before shutting down

			break;
		default:
			break;

		}
		Log.fine("after dis");

	}

}
