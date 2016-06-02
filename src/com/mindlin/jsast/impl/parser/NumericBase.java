package com.mindlin.jsast.impl.parser;

public enum NumericBase {
	BINARY(2),
	OCTAL(8),
	DECIMAL(10),
	HEXDECIMAL(16);
	static final char[] chars = "0123456789ABCDEF".toCharArray();
	final int exponent;
	NumericBase(int exponent) {
		this.exponent = exponent;
	}
	public int getExponent() {
		return exponent;
	}
	public boolean isDigit(char c) {
		return false;
	}
}
