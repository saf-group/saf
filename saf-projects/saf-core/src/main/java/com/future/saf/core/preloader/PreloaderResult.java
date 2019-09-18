package com.future.saf.core.preloader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class PreloaderResult {

	private static volatile boolean preloadFlag = false;

	public static void complete() {
		preloadFlag = true;
	}

	public static boolean isComplete() {
		return preloadFlag;
	}
}
