package com.mindlin.jsast.impl.parser;

public enum JSOperator {
	//Reference: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators
	EQUAL("A==B"),
	NOT_EQUAL("A!=B"),
	STRICT_EQUAL("A===B"),
	STRICT_NOT_EQUAL("A!==B"),
	GREATER_THAN("A>B"),
	GREATER_THAN_EQUAL("A>=B"),
	LESS_THAN_EQUAL("A<=B"),
	LESS_THAN("A<B"),
	
	INCREMENT("++"),
	DECREMENT("--"),
	
	PLUS("+","A+B"),
	MINUS("-","-A","A-B"),
	MULTIPLICATION("*","A*B"),
	DIVISION("/","A/B"),
	REMAINDER("%","A%B"),
	EXPONENTIATION("**","A**B"),
	LEFT_SHIFT("<<","A<<B"),
	RIGHT_SHIFT(">>","A>>B"),
	UNSIGNED_RIGHT_SHIFT(">>>","A>>>B"),
	
	BITWISE_AND("&","A&B"),
	BITWISE_XOR("^","A^B"),
	BITWISE_OR("|","A|B"),
	BITWISE_NOT("~","~A"),
	
	LOGICAL_AND("&&","A&&B"),
	LOGICAL_OR("||","A||B"),
	LOGICAL_NOT("!","!A"),
	
	ASSIGNMENT("A=B"),
	ADDITION_ASSIGNMENT("A+=B"),
	SUBTRACTION_ASSIGNMENT("A-=B"),
	MULTIPLICATION_ASSIGNMENT("A*=B"),
	DIVISION_ASSIGNMENT("A/=B"),
	REMAINDER_ASSIGNMENT("A%=B"),
	EXPONENTIATION_ASSIGNMENT("A**=B"),
	LEFT_SHIFT_ASSIGNMENT("A<<=B"),
	RIGHT_SHIFT_ASSIGNMENT("A>>=B"),
	UNSIGNED_RIGHT_SHIFT_ASSIGNMENT("A>>>=B"),
	BITWISE_AND_ASSIGNMENT("A&=B"),
	BITWISE_XOR_ASSIGNMENT("A^=B"),
	BITWISE_OR_ASSIGNMENT("A|=B"),
	
	QUESTION_MARK("?","A?B:C"),
	COLON(":","A?B:C"),
	LEFT_PARENTHESIS("("),
	RIGHT_PARENTHESIS(")"),
	COMMA(",","A,B"),
	LAMBDA("=>")
	;
	final String operator;
	JSOperator(String operator, String...allotropes) {
		this.operator = operator;
	}
	public int length() {
		return operator.length();
	}
}
