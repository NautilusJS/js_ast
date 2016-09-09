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
	BOOLEAN_LITERAL,
	NUMERIC_LITERAL,
	STRING_LITERAL,
	TEMPLATE_LITERAL,
	NULL_LITERAL,
	/**
	 * An identifier (variable/function/object).
	 */
	IDENTIFIER, FUTURE, FUTURESTRICT
}