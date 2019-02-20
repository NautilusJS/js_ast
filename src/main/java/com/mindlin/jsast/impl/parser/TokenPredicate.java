package com.mindlin.jsast.impl.parser;

import java.util.function.Predicate;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;

/**
 * Commonly used predicates for matching certain classes of tokens
 * @author mailmindlin
 */
public class TokenPredicate {
	public static Predicate<Token> match(TokenKind kind, Object value) {
		return t -> t.matches(kind, value);
	}
	
	public static final Predicate<Token> VARIABLE_START = t->(t.isKeyword() && (t.getValue() == JSKeyword.VAR || t.getValue() == JSKeyword.LET || t.getValue() == JSKeyword.CONST));
	public static final Predicate<Token> IN_OR_OF = t->(t.isKeyword() && (t.getValue() == JSKeyword.IN || t.getValue() == JSKeyword.OF));
	public static final Predicate<Token> PARAMETER_TYPE_START = t->(t.isOperator() && (t.getValue() == JSOperator.QUESTION_MARK || t.getValue() == JSOperator.COLON || t.getValue() == JSOperator.ASSIGNMENT));
	public static final Predicate<Token> CALL_SIGNATURE_START = t -> (t.isOperator() && (t.getValue() == JSOperator.LEFT_PARENTHESIS || t.getValue() == JSOperator.LESS_THAN));
	public static final Predicate<Token> START_OF_PARAMETER = t->{
		if (t.isIdentifier())
			return true;
		if (!t.isOperator())
			return false;
		JSOperator value = t.<JSOperator>getValue();
		return value == JSOperator.LEFT_BRACE || value == JSOperator.LEFT_BRACKET || value == JSOperator.SPREAD;
	};
	public static final Predicate<Token> TYPE_CONTINUATION = t->(t.isOperator() && (t.getValue() == JSOperator.VBAR || t.getValue() == JSOperator.AMPERSAND));
	public static final Predicate<Token> CAN_FOLLOW_MODIFIER = t -> {
		Object value = t.getValue();
		switch (t.getKind()) {
			case OPERATOR:
				return value == JSOperator.ASTERISK
						|| value == JSOperator.SPREAD
						|| value == JSOperator.LEFT_BRACKET
						|| value == JSOperator.LEFT_BRACE;
			case STRING_LITERAL:
			case NUMERIC_LITERAL:
			case IDENTIFIER:
			case KEYWORD:
				return true;
			default:
				return false;
		}
	};
	public static final Predicate<Token> HERITAGE_START = t -> (t.isKeyword() && (t.getValue() == JSKeyword.EXTENDS || t.getValue() == JSKeyword.IMPLEMENTS));
	public static final Predicate<Token> UPDATE_OPERATOR = t->(t.isOperator() && (t.getValue() == JSOperator.INCREMENT || t.getValue() == JSOperator.DECREMENT));
	public static final Predicate<Token> RIGHT_BRACE = t -> t.matchesOperator(JSOperator.RIGHT_BRACE);
	/**
	 * TokenPredicate can't be instantiated
	 */
	private TokenPredicate() {}
}
