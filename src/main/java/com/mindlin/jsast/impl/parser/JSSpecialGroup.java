package com.mindlin.jsast.impl.parser;

public enum JSSpecialGroup {
	SEMICOLON,
	/**
	 * Denotes the end of a line. This is a semicolon in an ideal world,
	 * but it could be a newline or something that isn't escaped.
	 */
	EOL,
	/**
	 * Denotes the end of the file. Not that this doesn't actually map to a character in the file.
	 */
	EOF;
}
