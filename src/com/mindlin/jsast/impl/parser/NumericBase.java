package com.mindlin.jsast.impl.parser;

public enum NumericBase {
	BINARY(2),
	OCTAL(8),
	DECIMAL(10),
	HEXDECIMAL(16);
	final int exponent;
	NumericBase(int exponent) {
		this.exponent = exponent;
	}
	public int getExponent() {
		return exponent;
	}
}
