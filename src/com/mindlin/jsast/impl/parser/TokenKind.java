package com.mindlin.jsast.impl.parser;

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
	LITERAL, IDENTIFIER, IR, FUTURE, FUTURESTRICT
}