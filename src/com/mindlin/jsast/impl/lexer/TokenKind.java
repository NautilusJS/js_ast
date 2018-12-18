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
	 * A JS keyword
	 */
	KEYWORD,
	/**
	 * A JS boolean literal value
	 */
	BOOLEAN_LITERAL,
	NUMERIC_LITERAL,
	STRING_LITERAL,
	TEMPLATE_LITERAL,
	/**
	 * A js null literal. Literally {@code null}.
	 */
	NULL_LITERAL,
	/**
	 * A js regular expression literal
	 */
	REGEX_LITERAL,
	/**
	 * An identifier (variable/function/object).
	 */
	IDENTIFIER,
	COMMENT;
}