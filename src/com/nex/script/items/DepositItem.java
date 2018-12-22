package com.nex.script.items;

import java.util.ArrayList;
import java.util.List;

public class DepositItem{
	
	private List<Integer> items;
	private DepositType type;
	public enum DepositType{
		DEPOSIT_ALL, DEPOSIT_ALL_EXCEPT
	}
	public DepositItem(DepositType type, List<Integer> items) {
		setType(type);
		setItems(items);
	}
	public DepositItem(DepositType type, int name) {
		setType(type);
		items = new ArrayList<Integer>();
		items.add(name);
	}
	public List<Integer> getItems() {
		return items;
	}
	public void setItems(List<Integer> items) {
		this.items = items;
	}
	public DepositType getType() {
		return type;
	}
	public void setType(DepositType type) {
		this.type = type;
	}
}
