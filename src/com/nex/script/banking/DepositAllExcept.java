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

	private String itemToKeep;
	
	public DepositAllExcept(String itemToKeep) {
		this.itemToKeep = itemToKeep;
	}
	public void execute() {
		if(Bank.isOpen()) {
			Bank.depositAllExcept(itemToKeep);
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
		for(Item item : Inventory.getItems()) {
			if(!item.getName().equals(itemToKeep)) {
				return false;
			}
		}
		return true;
	}
	


}
