package com.mindlin.jsast.exception;

public class JSEOFException extends JSSyntaxException {
	public JSEOFException(long position) {
		this("Unexpected EOF", position);
	}
	public JSEOFException(String message, long position) {
		super(message, position);
		// TODO Auto-generated constructor stub
	}
	
}
