package com.mindlin.nautilus.exception;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.fs.SourceRange;

public class JSUnsupportedException extends JSException {
	private static final long serialVersionUID = -1291736363417012727L;
	
	public JSUnsupportedException(String feature, SourcePosition position) {
		super("Unsupported feature '" + feature + "' at " + position, position);
	}
	
	public JSUnsupportedException(String feature, SourceRange position) {
		super("Unsupported feature '" + feature + "' at " + position, position);
	}
}
