package com.nex.script.items;



public class WithdrawItem extends RequiredItem{
	
	
	public WithdrawItem(int itemID, int itemAmount, String itemName, int buyAmount) {
		super(itemID, itemAmount, buyAmount, itemName);
	}
	
	public WithdrawItem(RSItem item, int itemAmount, int buyAmount) {
		super(item.getId(), itemAmount, buyAmount, item.getName());
	}
	
	public WithdrawItem(boolean coins, int itemAmount) {
		super(995, itemAmount, itemAmount, "Coins");
	}
}
	




