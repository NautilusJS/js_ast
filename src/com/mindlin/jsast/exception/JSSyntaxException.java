package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.fs.SourceRange;

public class JSSyntaxException extends JSException {
	private static final long serialVersionUID = 8236793790942441074L;

	@Deprecated
	public JSSyntaxException(String message) {
		super(message);
	}
	
	public JSSyntaxException(String message, SourcePosition position) {
		this(message, SourceRange.startingFrom(position));
	}
	
	public JSSyntaxException(String message, SourcePosition start, SourcePosition end) {
		this(message, new SourceRange(start, end));
	}
	
	public JSSyntaxException(String message, SourceRange pos) {
		super(message + " at " + pos, pos);
	}
	
	public JSSyntaxException(String message, JSException cause) {
		this(message, cause.getPosition(), cause);
	}
	
	public JSSyntaxException(String message, SourceRange position, Throwable cause) {
		super(message + " at " + position, position, cause);
	}
}
