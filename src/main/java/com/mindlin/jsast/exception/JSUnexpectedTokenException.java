package com.mindlin.jsast.exception;

import com.mindlin.jsast.fs.SourceRange;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;

public class JSUnexpectedTokenException extends JSSyntaxException {
	private static final long serialVersionUID = -5669873573919357134L;

	public JSUnexpectedTokenException(Token token) {
		this("Unexpected token " + token, token == null ? null : token.getRange());
	}

	public JSUnexpectedTokenException(Token token, TokenKind expectedKind) {
		this("Unexpected token " + token + " (expected kind: " + expectedKind + ")", token.getRange());
	}

	public JSUnexpectedTokenException(Token token, Object expectedValue) {
		this("Unexpected token " + token + " (expected value: " + expectedValue + ")", token.getRange());
	}
	
	public JSUnexpectedTokenException(Token token, String reason) {
		this("" + token + reason, token.getRange());
	}
	
	public JSUnexpectedTokenException(String message, SourceRange position) {
		super(message, position);
	}
}
