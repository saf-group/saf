package com.future.saf.core.util;

public class NumberUtil {
	public static double getDouble(double value) {
		return (Double.isNaN(value) ? 0D : value);
	}
}
