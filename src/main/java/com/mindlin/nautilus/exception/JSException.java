package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.fs.SourceRange;

public class JSException extends RuntimeException {
	private static final long serialVersionUID = -2503501961662070326L;
	
	private final SourceRange position;
	
	public JSException() {
		super();
		this.position = null;
	}

	protected JSException(String message, SourceRange position, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.position = position;
	}

	public JSException(String message, Throwable cause) {
		super(message, cause);
		this.position = null;
	}

	public JSException(String message) {
		this(message, (SourceRange) null);
	}
	
	public JSException(String message, SourcePosition start) {
		this(message, (SourceRange) (start == null ? null : SourceRange.startingFrom(start)));
	}
	
	public JSException(String message, SourceRange position) {
		super(message);
		this.position = position;
	}
	
	public JSException(String message, SourceRange position, Throwable cause) {
		super(message, cause);
		this.position = position;
	}

	public JSException(Throwable cause) {
		super(cause);
		this.position = null;
	}
	
	public SourceRange getPosition() {
		return this.position;
	}
}
