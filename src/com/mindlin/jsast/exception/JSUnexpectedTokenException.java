package com.mindlin.jsast.exception;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;

public class JSUnexpectedTokenException extends JSSyntaxException {
	private static final long serialVersionUID = -5669873573919357134L;
	public JSUnexpectedTokenException(Token token) {
		super("Unexpected token " + token, token.getStart());
	}
	public JSUnexpectedTokenException(Token token, TokenKind expectedKind) {
		super("Unexpected token " + token + " (expected kind: " + expectedKind + ")", token.getStart());
	}
	public JSUnexpectedTokenException(Token token, Object expectedValue) {
		super("Unexpected token " + token + " (expected value: " + expectedValue + ")", token.getStart());
	}
}
