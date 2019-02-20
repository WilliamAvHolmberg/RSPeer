package com.nex.script.items;

public abstract class RequiredItem  {
	private int itemID;
	private int itemAmount;
	private int buyAmount;
	private String itemName;
	
	public RequiredItem(int itemID, int itemAmount, int buyAmount, String itemName) {
		this.itemID = itemID;
		this.itemAmount = itemAmount;
		this.buyAmount = buyAmount;
		this.itemName = itemName;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return  false;
		if (!(obj instanceof RequiredItem)) return false;
		RequiredItem other = (RequiredItem)obj;
		if(other.itemName == null || this.itemName == null) {
			if(other.itemID != this.itemID)
				return false;
		}
		else if(other.itemName != this.itemName) return false;
		if(other.buyAmount != this.buyAmount) return false;
		if(other.itemAmount != this.itemAmount) return false;
		return true;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getAmount() {
		return itemAmount;
	}
	
	public int getBuyAmount() {
		return buyAmount;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemID(int id) {
		this.itemID = id;
	}
	
	public void setAmount(int amount) {
		this.itemAmount = amount;
	}
	
	public void setBuyAmount(int amount) {
		this.buyAmount = amount;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String toString() {
		return "Amount:" + itemAmount + "...ID:" + itemID;
	}
}
