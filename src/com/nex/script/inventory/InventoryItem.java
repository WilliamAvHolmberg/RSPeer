package com.nex.script.inventory;

import com.nex.script.items.RSItem;

public class InventoryItem {
	
	private int amount;
	private RSItem item;
	private int buyAmount;
	
	public InventoryItem(int amount, RSItem item, int buyAmount) {
		this.amount = amount;
		this.item = item;
		this.buyAmount = buyAmount;
	}
	public int getAmount() {
		return amount;
	}

	public RSItem getItem() {
		return item;
	}
	
	public int getBuyAmount() {
		return buyAmount;
	}
	public InventoryItem setAmount(int amount) {
		this.amount = amount;
		return this;
	}

}
