package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.FilePosition;

public class JSSyntaxException extends JSException {
	private static final long serialVersionUID = 8236793790942441074L;

	public JSSyntaxException(String message) {
		super(message);
	}

	public JSSyntaxException(String message, long position) {
		this(message + " at " + position);
	}
	
	public JSSyntaxException(String message, FilePosition position) {
		this(message + " at " + position);
	}
	
	public JSSyntaxException(String message, long start, long end) {
		this(message + " at " + start + "..." + end);
	}
	
	public JSSyntaxException(String message, FilePosition start, FilePosition end) {
		this(message + " at " + start + "..." + end);
	}
	
	public JSSyntaxException(String message, long position, Throwable cause) {
		super(message + " at " + position, cause);
	}
}
