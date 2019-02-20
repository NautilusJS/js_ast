package com.mindlin.jsast.impl.lexer;

/**
 * An enumeration of the possible numberic literal types.
 * @author mailmindlin
 */
public enum NumericLiteralType {
	/**
	 * A binary literal, starting with '0b'.
	 */
	BINARY(2),
	/**
	 * An octal literal starting with '0o'.
	 * @see #OCTAL_IMPLICIT
	 */
	OCTAL(8),
	/**
	 * An implicit octal, starting with '0'. Can be upgraded to {@link #DECIMAL DECIMAL} if an '8' or '9' show up
	 * later in the literal.
	 */
	OCTAL_IMPLICIT(8),
	/**
	 * A decimal literal. Either does not have a prefix, or was upgraded from {@link #OCTAL_IMPLICIT OCTAL_IMPLICIT}
	 */
	DECIMAL(10),
	/**
	 * A hexdecimal literal, starting with '0x'.
	 */
	HEXDECIMAL(16);
	static final char[] chars = "0123456789ABCDEF".toCharArray();
	final int exponent;
	NumericLiteralType(int exponent) {
		this.exponent = exponent;
	}
	public int getExponent() {
		return exponent;
	}
	public boolean isDigit(char c) {
		//TODO finish (or remove)
		throw new UnsupportedOperationException();
	}
}
