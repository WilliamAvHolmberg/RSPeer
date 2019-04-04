package com.nex.script.items;

import org.rspeer.ui.Log;

import com.nex.script.Exchange;

public class GEItem extends RequiredItem {

	private int itemPrice;
	private int originalItemPrice;
	private int timesRaised = 0;
	
	public GEItem(RequiredItem requiredItem) {
		super(requiredItem.getItemID(), requiredItem.getAmount(), requiredItem.getBuyAmount(), requiredItem.getItemName());
		setItemPrice(Exchange.getPrice(getItemID()));
		if(getBuyAmount() < 5) {
			if(itemPrice < 10)
				itemPrice += 5;
			else
				itemPrice += 100;
		}
	}
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	private void setItemPrice(int price) {
		this.itemPrice = price;
		if(this.itemPrice < 5000 && getAmount() == 1) { //added to make it faster to buy items such as gear etc. 
			this.itemPrice = this.itemPrice * 2;
		}
		if(originalItemPrice == 0)
			originalItemPrice = price;

	}
	
	public int getTotalPrice() {
		return (int) (getItemPrice() * getBuyAmount() * 1.3);
	}
	
	public int getItemPrice() {
		if(itemPrice <= 0) {
			return 25;
		}
		return itemPrice;
	}
	public int getOriginalItemPrice(){
		return originalItemPrice;
	}
	public int getTimesRaised(){
		return timesRaised;
	}

	public void raiseItemPrice() {
		if(this.itemPrice > this.originalItemPrice * 10) {
			timesRaised++;
			Log.fine("Item price is fucked up. lets not raise more");
			return;
		}
		this.itemPrice = (int) (this.itemPrice * 1.25) + 1;
	
	}

	

}
