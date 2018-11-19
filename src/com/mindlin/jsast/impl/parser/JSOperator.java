package com.mindlin.jsast.impl.parser;

public enum JSOperator {
	// Reference:
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators
	AT_SYMBOL(1, "@"),
	EQUAL(2, "==", "A==B"),
	NOT_EQUAL(2, "!=", "A!=B"),
	STRICT_EQUAL(2, "===", "A===B"),
	STRICT_NOT_EQUAL(2, "!==", "A!==B"),
	GREATER_THAN(2, ">", "A>B"),
	GREATER_THAN_EQUAL(2, ">=", "A>=B"),
	LESS_THAN_EQUAL(2, "<=", "A<=B"),
	LESS_THAN(2, "<", "A<B"),

	INCREMENT(1, "++", "A++", "++A"),
	DECREMENT(1, "--", "B++", "++B"),

	PLUS(2, "+", "A+B"),
	MINUS(2, "-", "-A", "A-B"),
	MULTIPLICATION(2, "*", "A*B"),
	DIVISION(2, "/", "A/B"),
	REMAINDER(2, "%", "A%B"),
	EXPONENTIATION(2, "**", "A**B"),
	LEFT_SHIFT(2, "<<", "A<<B"),
	RIGHT_SHIFT(2, ">>", "A>>B"),
	UNSIGNED_RIGHT_SHIFT(2, ">>>", "A>>>B"),

	BITWISE_AND(2, "&", "A&B"),
	BITWISE_XOR(2, "^", "A^B"),
	BITWISE_OR(2, "|", "A|B"),
	BITWISE_NOT(1, "~", "~A"),

	LOGICAL_AND(2, "&&", "A&&B"),
	LOGICAL_OR(2, "||", "A||B"),
	LOGICAL_NOT(1, "!", "!A"),

	ASSIGNMENT(true, 2, "=", "A=B"),
	ADDITION_ASSIGNMENT(true, 2, "+=", "A+=B"),
	SUBTRACTION_ASSIGNMENT(true, 2, "-=", "A-=B"),
	MULTIPLICATION_ASSIGNMENT(true, 2, "*=", "A*=B"),
	DIVISION_ASSIGNMENT(true, 2, "/=", "A/=B"),
	REMAINDER_ASSIGNMENT(true, 2, "%=", "A%=B"),
	EXPONENTIATION_ASSIGNMENT(true, 2, "**=", "A**=B"),
	LEFT_SHIFT_ASSIGNMENT(true, 2, "<<=", "A<<=B"),
	RIGHT_SHIFT_ASSIGNMENT(true, 2, ">>=", "A>>=B"),
	UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(true, 2, ">>>=", "A>>>=B"),
	BITWISE_AND_ASSIGNMENT(true, 2, "&=", "A&=B"),
	BITWISE_XOR_ASSIGNMENT(true, 2, "^=", "A^=B"),
	BITWISE_OR_ASSIGNMENT(true, 2, "|=", "A|=B"),

	QUESTION_MARK(3, "?", "A?B:C"),
	COLON(3, ":", "A?B:C"),
	LEFT_PARENTHESIS(0, "("),
	RIGHT_PARENTHESIS(0, ")"),
	COMMA(2, ",", "A,B"),
	LAMBDA(2, "=>"),

	PERIOD(2, "."),
	SPREAD(1, "..."),;

	final String operator;
	final int precedence;
	final int arity;
	final boolean assignment;

	JSOperator(boolean assignment, int precedence, int arity, String operator, String... allotropes) {
		this.assignment = assignment;
		this.operator = operator;
		this.arity = arity;
		this.precedence = precedence;
	}

	JSOperator(boolean assignment, int arity, String operator, String... allotropes) {
		this(assignment, -1, arity, operator, allotropes);
	}

	JSOperator(int precedence, int arity, String operator, String... allotropes) {
		this(false, precedence, arity, operator, allotropes);
	}

	JSOperator(int arity, String operator, String... allotropes) {
		this(-1, arity, operator, allotropes);
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
