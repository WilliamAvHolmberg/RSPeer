package com.nex.task.helper;

public class MathHelper {

	public static long getPerHour(long time, long amount) {
		return (int) (amount * (3600000.0 / time));
	}
}
