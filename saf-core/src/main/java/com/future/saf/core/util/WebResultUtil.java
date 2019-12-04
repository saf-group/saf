package com.future.saf.core.util;

import com.future.saf.core.web.WebResult;

public class WebResultUtil {

	public static <T> WebResult<T> newResult(int code, String msg, T data) {
		return new WebResult<T>(code, msg, data);
	}

	public static <T> WebResult<T> success(T data) {
		return new WebResult<T>(WebResult.CODE_SUCCESS, WebResult.MSG_NONE, data);
	}

	public static <T> WebResult<T> error(int code, String msg) {
		return new WebResult<T>(code, msg, null);
	}
}
