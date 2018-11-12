package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.fs.SourceRange;

public class JSSyntaxException extends JSException {
	private static final long serialVersionUID = 8236793790942441074L;

	public JSSyntaxException(String message) {
		super(message);
	}

	@Deprecated
	public JSSyntaxException(String message, long position) {
		this(message + " at " + position);
	}
	
	public JSSyntaxException(String message, SourcePosition position) {
		this(message + " at " + position);
	}
	
	public JSSyntaxException(String message, SourcePosition start, SourcePosition end) {
		this(message, new SourceRange(start, end));
	}
	
	public JSSyntaxException(String message, SourceRange pos) {
		this(message + " at " + pos);
	}
	
	public JSSyntaxException(String message, JSException cause) {
		super(message + " at " + cause.getPosition(), cause);
	}
	
	public JSSyntaxException(String message, SourcePosition position, Throwable cause) {
		super(message + " at " + position, cause);
	}
	
	@Deprecated
	public JSSyntaxException(String message, long position, Throwable cause) {
		super(message + " at " + position, cause);
	}
}
