package com.nex.task.mule;

import java.awt.Graphics2D;
import java.util.function.BooleanSupplier;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import com.nex.script.Nex;
import com.nex.script.handler.TaskHandler;
import com.nex.task.NexTask;


public abstract class Mule extends NexTask{
	
	protected int world;
	protected int itemID;
	protected int itemAmount;
	protected int startAmount;
	protected String tradeName;
	protected boolean banked = false;
	protected boolean soldItems = false;
	public boolean tradeIsCompleted = false;
	


	public Mule(int world, int itemID, int itemAmount, int startAmount, String muleName) {
		setWorld(world);
		setItemID(itemID);
		setItemAmount(itemAmount);
		setStartAmount(startAmount);
		setTradeName(muleName);
	}
	
	protected Player getMule(String name) {
		for (Player player : Players.getLoaded()) {
			if (player.getName().toLowerCase().equals(name.toLowerCase())) {
				return player;
			}
		}
		return null;
	}
	


	public int getWorld() {
		return world;
	}

	public void setWorld(int world) {
		this.world = world;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public int getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(int itemAmount) {
		this.itemAmount = itemAmount;
	}

	public int getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(int startAmount) {
		this.startAmount = startAmount;
	}
	
	public boolean getBanked() {
		return banked;
	}
	
	public void setBanked(boolean bool) {
		this.banked = bool;
	}

	public String getTradeName() {
		return tradeName;
	}
	
	
	@Override
	public boolean isFinished() {
		Log.fine("are we done?" + tradeIsCompleted);
		if(tradeIsCompleted) {
			Log.fine("Trade completed from mess");
			return true;
		}	
		if (getTimeLeft() <= 0) {
			Log.fine("no time left");
			return true;
		}
		return false;

	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public void setSoldItems(boolean bool) {
		this.soldItems = bool;
	}
	public boolean soldItems() {
		return soldItems;
	}
	
	@Override
	public void removeTask() {
		// TODO send mule done with info
		
	}
	
	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	



}
