package com.nex.script.items;

import com.nex.script.Exchange;

public class GEItem extends RequiredItem {

	private int itemPrice;
	
	public GEItem(RequiredItem requiredItem) {
		super(requiredItem.getItemID(), requiredItem.getAmount(), requiredItem.getBuyAmount(), requiredItem.getItemName());
		setItemPrice(Exchange.getPrice(getItemID()));
	}

	private void setItemPrice(int price) {
		this.itemPrice = price;
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

	public void raiseItemPrice() {
		this.itemPrice = (int) (this.itemPrice * 1.25) + 1;
	}

	

}
