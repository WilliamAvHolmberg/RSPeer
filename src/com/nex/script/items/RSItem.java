package com.nex.script.items;

import java.util.ArrayList;
import java.util.List;

import com.nex.script.Exchange;

public class RSItem {
	public static List<RSItem> cachedItems = new ArrayList<RSItem>();
	private String name;
	private int id;
	private int itemPrice = -5;
	public RSItem(String name, int id) {
		this.setName(name);
		this.setId(id);
		cachedItems.add(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getItemPrice() {
		if(itemPrice == -5) {
			itemPrice = Exchange.getPrice(id);
			return itemPrice;
		}else {
			return itemPrice;
		}
	}
	
	public static RSItem getItem(String name, int id) {
		for(RSItem item : cachedItems) {
			if(item.getId() == id) {
				return item;
			}
		}
		return new RSItem(name, id);
		
	}
	

}
