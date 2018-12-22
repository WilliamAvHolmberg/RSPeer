package com.nex.task.actions.mule;

import org.rspeer.runetek.api.component.Trade;
import org.rspeer.ui.Log;

public class WithdrawItemFromPlayer extends TradeAction {



	public WithdrawItemFromPlayer(String playerToTrade, int itemID, int itemAmount) {
		super(playerToTrade, itemID, itemAmount);
	}

	public int execute() {
		if(!Trade.isOpen()) {
			initiateTrade(playerToTrade);
		}else if(firstTradeScreenIsOkay(itemID, itemAmount)) {
			acceptFirstScreen();
		}else if(Trade.isOpen(true)) {
			acceptFirstScreen();
		}else {
			Log.fine("Waiting for mule to do move.");
		}
		return 200;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Trade with Slave";
	}

}
