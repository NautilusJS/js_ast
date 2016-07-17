package com.mindlin.jsast.impl.lexer;

public enum TokenKind {
	/**
	 * Semicolon, EOL, EOF, etc.
	 */
	SPECIAL,
	/**
	 * 
	 */
	OPERATOR,
	/**
	 * [,],{,}
	 */
	BRACKET,
	/**
	 * A JS keyword
	 */
	KEYWORD,
	/**
	 * A JS literal value
	 */
	LITERAL,
	/**
	 * An identifier (variable/function/object).
	 */
	IDENTIFIER, FUTURE, FUTURESTRICT
}