package com.future.saf.core.util;

import org.apache.logging.log4j.util.ProcessIdUtil;

public class PIDUtil {

	private static String currentPID = null;

	public static final String getProcessID() {
		if (currentPID == null) {
			synchronized (PIDUtil.class) {
				if (currentPID == null) {
					currentPID = ProcessIdUtil.getProcessId();
				}
			}
		}
		return currentPID;
	}

	public static void main(String[] args) {
		System.out.println(getProcessID());
		System.out.println(ProcessIdUtil.getProcessId());
		System.out.println(PIDUtil.class.getName());
	}
}
