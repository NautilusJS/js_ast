package com.mindlin.jsast.exception;

public class JSEOFException extends JSSyntaxException {
	private static final long serialVersionUID = -193254378704376443L;
	
	public JSEOFException(long position) {
		this("Unexpected EOF", position);
	}
	
	public JSEOFException(String message, long position) {
		super(message, position);
		// TODO Auto-generated constructor stub
	}
	
}
