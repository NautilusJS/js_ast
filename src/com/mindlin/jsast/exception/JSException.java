package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.FilePosition;

public class JSException extends RuntimeException {
	private static final long serialVersionUID = -2503501961662070326L;
	
	private final FilePosition position;
	
	public JSException() {
		super();
		this.position = null;
	}

	public JSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.position = null;
	}

	public JSException(String message, Throwable cause) {
		super(message, cause);
		this.position = null;
	}

	public JSException(String message) {
		super(message);
		this.position = null;
	}

	public JSException(Throwable cause) {
		super(cause);
		this.position = null;
	}
	
	public FilePosition getPosition() {
		return this.position;
	}
}
