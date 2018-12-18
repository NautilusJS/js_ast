package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.SourcePosition;

public class JSEOFException extends JSSyntaxException {
	private static final long serialVersionUID = -193254378704376443L;
	
	@Deprecated
	public JSEOFException(SourcePosition position) {
		this("Unexpected EOF", position);
	}
	
	public JSEOFException(String message, SourcePosition position) {
		super(message, position);
	}
}
