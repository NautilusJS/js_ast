package com.mindlin.jsast.exception;

public class JSException extends RuntimeException {
	private static final long serialVersionUID = -2503501961662070326L;

	public JSException() {
		super();
	}

	public JSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JSException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSException(String message) {
		super(message);
	}

	public JSException(Throwable cause) {
		super(cause);
	}

}
