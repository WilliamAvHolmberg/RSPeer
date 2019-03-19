package com.nex.script.items;

import com.nex.script.Exchange;

public class GEItem extends RequiredItem {

	private int itemPrice;
	private int originalItemPrice;
	private int timesRaised = 0;
	
	public GEItem(RequiredItem requiredItem) {
		super(requiredItem.getItemID(), requiredItem.getAmount(), requiredItem.getBuyAmount(), requiredItem.getItemName());
		setItemPrice(Exchange.getPrice(getItemID()));
	}
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	private void setItemPrice(int price) {
		this.itemPrice = price;
		if(originalItemPrice == 0)
			originalItemPrice = price;
	}
	
	public int getTotalPrice() {
		return (int) (getItemPrice() * getAmount() * 1.3);
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
		this.itemPrice = (int) (this.itemPrice * 1.25) + 1;
		timesRaised++;
	}

	

}
