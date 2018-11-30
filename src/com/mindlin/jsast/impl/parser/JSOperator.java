package com.mindlin.jsast.impl.parser;

public enum JSOperator {
	// Reference:
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators
	AT_SYMBOL(1, "@"),
	EQUAL(2, "=="),
	NOT_EQUAL(2, "!="),
	STRICT_EQUAL(2, "==="),
	STRICT_NOT_EQUAL(2, "!=="),
	GREATER_THAN(2, ">"),
	GREATER_THAN_EQUAL(2, ">="),
	LESS_THAN_EQUAL(2, "<="),
	LESS_THAN(2, "<"),

	INCREMENT(1, "++"),
	DECREMENT(1, "--"),

	PLUS(2, "+"),
	MINUS(2, "-"),
	ASTERISK(2, "*"),
	DIVISION(2, "/"),
	REMAINDER(2, "%"),
	EXPONENTIATION(2, "**"),
	LEFT_SHIFT(2, "<<"),
	RIGHT_SHIFT(2, ">>"),
	UNSIGNED_RIGHT_SHIFT(2, ">>>"),

	AMPERSAND(2, "&"),
	BITWISE_XOR(2, "^"),
	VBAR(2, "|"),
	BITWISE_NOT(1, "~"),

	LOGICAL_AND(2, "&&"),
	LOGICAL_OR(2, "||"),
	LOGICAL_NOT(1, "!"),

	ASSIGNMENT(true, 2, "="),
	ADDITION_ASSIGNMENT(true, 2, "+="),
	SUBTRACTION_ASSIGNMENT(true, 2, "-="),
	MULTIPLICATION_ASSIGNMENT(true, 2, "*="),
	DIVISION_ASSIGNMENT(true, 2, "/="),
	REMAINDER_ASSIGNMENT(true, 2, "%="),
	EXPONENTIATION_ASSIGNMENT(true, 2, "**="),
	LEFT_SHIFT_ASSIGNMENT(true, 2, "<<="),
	RIGHT_SHIFT_ASSIGNMENT(true, 2, ">>="),
	UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(true, 2, ">>>="),
	BITWISE_AND_ASSIGNMENT(true, 2, "&="),
	BITWISE_XOR_ASSIGNMENT(true, 2, "^="),
	BITWISE_OR_ASSIGNMENT(true, 2, "|="),

	QUESTION_MARK(3, "?"),
	COLON(3, ":"),
	LEFT_PARENTHESIS(0, "("),
	RIGHT_PARENTHESIS(0, ")"),
	LEFT_BRACKET(0, "["),
	RIGHT_BRACKET(0, "]"),
	LEFT_BRACE(0, "{"),
	RIGHT_BRACE(0, "}"),
	COMMA(2, ","),
	LAMBDA(2, "=>"),

	PERIOD(2, "."),
	SPREAD(1, "..."),;

	final String operator;
	final int precedence;
	final int arity;
	final boolean assignment;

	JSOperator(boolean assignment, int precedence, int arity, String operator) {
		this.assignment = assignment;
		this.operator = operator;
		this.arity = arity;
		this.precedence = precedence;
	}

	JSOperator(boolean assignment, int arity, String operator) {
		this(assignment, -1, arity, operator);
	}

	JSOperator(int precedence, int arity, String operator) {
		this(false, precedence, arity, operator);
	}

	JSOperator(int arity, String operator) {
		this(-1, arity, operator);
	}
	
	public String getText() {
		return operator;
	}

	public int length() {
		return operator.length();
	}

	public boolean isAssignment() {
		return this.assignment;
	}

	public int precedence() {
		return this.precedence;
	}

	public int arity() {
		return arity;
	}
}
