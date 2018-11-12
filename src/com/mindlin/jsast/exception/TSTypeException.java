package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.SourceRange;

public class TSTypeException extends JSException {
	private static final long serialVersionUID = 33413686264186057L;

	public TSTypeException(String message) {
		this(message, null);
	}
	
	public TSTypeException(String message, SourceRange position) {
		this(message, position, null);
	}
	
	public TSTypeException(String message, SourceRange position, Throwable cause) {
		super(message, position, cause);
	}
}
