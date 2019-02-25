package com.nex.script.banking;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.ui.Log;

import com.nex.script.banking.BankEvent.Type;

public class DepositAllExcept extends BankEvent{

	private String[] itemsToKeep;
	
	public DepositAllExcept(String ... itemsToKeep) {
		this.itemsToKeep = itemsToKeep;
	}
	public void execute() {
		if(Bank.isOpen()) {
			Bank.depositAllExcept(itemsToKeep);
		}else {
			Bank.open();
		}
	}
	
	@Override
	public Type getBankType() {
		return Type.DEPOSIT;
	}
	@Override
	public boolean isFinished() {
		if (Inventory.containsAnyExcept(itemsToKeep))
			return false;
		return true;
	}
	


}
