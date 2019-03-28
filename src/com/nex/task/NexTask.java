package com.nex.task;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.ObjectSpawnListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.ui.Log;

import com.nex.handler.gear.Gear;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.RSItem;

public abstract class NexTask implements RenderListener, ChatMessageListener, ObjectSpawnListener {
	
	protected long timeStarted = System.currentTimeMillis();
	protected long breakAfter;
	private String taskID;

	public abstract int loop();
	public abstract boolean isFinished();
	public abstract void removeTask();
	protected NexInventory requiredInventory = new NexInventory();
	protected List<RSItem> requiredItems = new ArrayList<RSItem>();
	protected Gear requiredGear = new Gear();
	
	public Gear getGear() {
		return this.requiredGear;
	}
	public void setGear(Gear gear) {
		this.requiredGear = gear;		
	}
	protected void setRequiredItems(List<RSItem> requiredItems) {
		this.requiredItems = requiredItems;
	}
	protected void setRequiredInventory(NexInventory inventory) {
		this.requiredInventory = inventory;
	}
	
	protected void addRequiredItem(RSItem item) {
		requiredItems.add(item);
	}
	protected void removeRequiredItem(RSItem item) {
		requiredItems.remove(item);
	}
	public List<RSItem> getRequiredItems(){
		List<RSItem> reqItems = requiredItems;
		if(requiredInventory != null && requiredInventory.getItems().size() > 0) {
			requiredInventory.getItems().forEach(item -> {
				reqItems.add(item.getItem());
			});
		}
		return reqItems;
	}
	public NexInventory getRequiredInventory() {
		return requiredInventory;
	}
	
	
	
	
	public long getTimeRanMS() {
		return System.currentTimeMillis() - timeStarted;
	}
	public String getTimeRanString() {
		return FormatTime(System.currentTimeMillis() - timeStarted);
	}
	public static String FormatTime(long r){
		long hours = TimeUnit.MILLISECONDS.toHours(r);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(r) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(r));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(r) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(r));
		String res = "";

		//Pretty Print the time so it will always be in this format 00:00:00
		if( hours < 10 ){
			res = res + "0" + hours + ":";
		}
		else{
			res = res + hours + ":";
		}
		if(minutes < 10){
			res = res + "0" + minutes + ":";
		}
		else{
			res = res + minutes + ":";
		}
		if(seconds < 10){
			res = res + "0" + seconds;
		}
		else{
			res = res + seconds;
		}
		return res;
	}
	
	public long getPerHour(long amount) {
		return (int) (amount * (3600000.0 / getTimeRanMS()));
	}
	
	//TODO
	public abstract String getLog();
	public void setBreakAfterTime(int breakAfter) {
		this.breakAfter = breakAfter;
		
	}
	public void setTaskID(String currentTaskID) {
		this.taskID = currentTaskID;
	}
	
	public long getTimeLeft() {
		return (timeStarted + breakAfter * 1000 * 60) - System.currentTimeMillis();
	}
	
	public String getTaskID() {
		if(taskID != null) {
			return taskID;
		}
		return "";
	}
}
