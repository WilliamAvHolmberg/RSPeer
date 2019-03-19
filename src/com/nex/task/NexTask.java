package com.nex.task;
import java.util.ArrayList;
import java.util.List;

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
