package com.mindlin.jsast.exception;

public class JSTypeError extends JSException {
	private static final long serialVersionUID = 8741290185246725973L;
	public JSTypeError(String msg) {
		super(msg);
	}
	public JSTypeError(Exception e) {
		super(e);
	}
}
