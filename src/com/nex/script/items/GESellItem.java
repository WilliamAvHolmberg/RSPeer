package com.nex.script.items;


import com.nex.script.Exchange;
import com.nex.task.tanning.TanningTask;

public class GESellItem {

	private int itemPrice;
	private boolean hasBeenWithdrawn = false;
	private boolean hasBeenSold = false; 
	private int itemID;
	private String itemName;

	public GESellItem(RSItem item){
		this(item, null);

	}
	public GESellItem(RSItem item, Boolean sellCheap) {
		setItemID(item.getId());
		setItemName(item.getName());
		//setAmount(amount);
		if(sellCheap == null)
		{
			sellCheap = true;
			if (TanningTask.ANY_HIDE_NAME.test(item.getName()))
				sellCheap = false;
		}
		if(sellCheap)
			setItemPrice(itemPrice);
		else
			setItemPrice(Exchange.getPrice(item.getId()));
	}
	
	public void setItemID(int id) {
		this.itemID = id;
	}
	public int getItemID() {
		return itemID;
	}
	
	public void setItemName(String name) {
		this.itemName = name;
	}
	public String getItemName() {
		return itemName;
	}

	private void setItemPrice(int price) {
		this.itemPrice = price;
	}
	

	
	public int getItemPrice() {
		return itemPrice;
	}
	
	public void setHasBeenWithdrawn(boolean hasBeenWithdrawn) {
		this.hasBeenWithdrawn = hasBeenWithdrawn;
	}

	public boolean hasBeenWithdrawnFromBank() {
		return hasBeenWithdrawn;
	}
	
	public void setHasBeenSold(boolean hasBeenSold) {
		this.hasBeenSold = hasBeenSold;
	}

	public boolean hasBeenSold() {
		return hasBeenSold;
	}

}
