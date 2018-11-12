package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.SourcePosition;

public class JSUnsupportedException extends RuntimeException {
	private static final long serialVersionUID = -1291736363417012727L;
	
	public JSUnsupportedException(String feature, SourcePosition position) {
		super("Unsupported feature '" + feature + "' at " + position);
	}
}
