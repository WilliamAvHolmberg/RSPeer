 package com.nex.task.actions.mule;

import org.rspeer.runetek.api.component.Trade;
import org.rspeer.ui.Log;

public class DepositItemToPlayer extends TradeAction{




	public DepositItemToPlayer(String playerToTrade, int itemID, int itemAmount) {
		super(playerToTrade, itemID, itemAmount);
	}

	public int execute() {
		if (!Trade.isOpen()) {
			initiateTrade(playerToTrade);
		} else if (Trade.isOpen(true)) {
			acceptFirstScreen();
		} else if (!firstTradeScreenIsOkay( itemID, itemAmount)) {
			Log.fine("lets add item");
			Log.fine("item id:" + itemID);
			addItemToTrade( itemID, itemAmount);
		} else if (Trade.isOpen(false)) {
			acceptFirstScreen();
		}
		return 200;
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Trade with Mule";
	}



}
