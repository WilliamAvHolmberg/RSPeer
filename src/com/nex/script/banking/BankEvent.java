package com.nex.script.banking;

import com.nex.task.IHandlerTask;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.movement.position.Area;

public abstract class BankEvent implements IHandlerTask {
	enum Type{
		WITHDRAW, DEPOSIT;
	}
	Area bankArea;
	public abstract void execute();
	public abstract Type getBankType();
	public abstract boolean isFinished();

	protected long timeStarted = System.currentTimeMillis();
	public long getTimeStarted() {
		return timeStarted;
	}
	
	public BankEvent setBankArea(Area area) {
		this.bankArea = area;
		return this;
	}
	
	public Area getBankArea() {
		if(bankArea == null) {
			return Area.surrounding(BankLocation.getNearest().getPosition(),10);
		}
		return bankArea;
	}
	
	

}
