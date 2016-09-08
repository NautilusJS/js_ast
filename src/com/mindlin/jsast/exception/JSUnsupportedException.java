package com.mindlin.jsast.exception;

public class JSUnsupportedException extends RuntimeException {
	private static final long serialVersionUID = -1291736363417012727L;

	public JSUnsupportedException(String feature, long position) {
		super("Unsupported feature '" + feature + "' at " + position);
	}
}
