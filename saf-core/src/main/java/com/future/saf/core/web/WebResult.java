package com.future.saf.core.web;

import java.io.Serializable;

public class WebResult<T> implements Serializable {

	private static final long serialVersionUID = 3937449233218325438L;

	public static final int CODE_SUCCESS = 0;
	public static final int CODE_FAILED = -1;
	public static final int CODE_FLOWCONTROL = -2;
	public static final int CODE_EXCEPTION = -3;
	public static final String MSG_NONE = "";

	private int code = CODE_SUCCESS;
	private String msg = MSG_NONE;
	private T data;

	public WebResult() {
	}

	public static WebResult<String> block() {
		return new WebResult<>(CODE_FLOWCONTROL, "request blocked by flowcontrol!", "");
	}

	public static WebResult<String> exception(String message) {
		return new WebResult<>(CODE_EXCEPTION, "request exception!", message);
	}

	public static <T> WebResult<T> build(int code, String msg, T data) {
		return new WebResult<>(code, msg, data);
	}

	public WebResult(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "WebResult{" + "code=" + code + ", msg='" + msg + '\'' + ", data=" + data + '}';
	}
}
