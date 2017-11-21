package com.mindlin.jsast.exception;

public class TSTypeException extends JSException {
	private static final long serialVersionUID = 33413686264186057L;

	public TSTypeException(String message) {
		super(message);
	}

	public TSTypeException(String message, long position) {
		this(message + " at " + position);
	}
	
	public TSTypeException(String message, long start, long end) {
		this(message + " at " + start + ":" + end);
	}
	
	public TSTypeException(String message, long position, Throwable cause) {
		super(message + " at " + position, cause);
	}
}
