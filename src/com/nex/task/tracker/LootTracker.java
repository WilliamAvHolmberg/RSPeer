package com.nex.task.tracker;

import com.nex.task.helper.MathHelper;

public class LootTracker implements Tracker{

	long timeStarted;
	int moneyGained;
	
	public LootTracker(long timeStarted) {
		this.timeStarted = timeStarted;
	}
	
	public void addGained(int gained) {
		moneyGained += gained;
	}
	@Override
	public long getTimeStarted() {
		return timeStarted;
	}

	@Override
	public long getTimeRan() {
		return System.currentTimeMillis() - timeStarted;
	}

	@Override
	public int getPerHour() {
		return (int) MathHelper.getPerHour(getTimeRan(), moneyGained);
	}

	@Override
	public int getGained() {
		return moneyGained;
	}

}
