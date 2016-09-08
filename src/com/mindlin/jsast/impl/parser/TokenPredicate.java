package com.mindlin.jsast.impl.parser;

import java.util.function.Predicate;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.TokenKind;

@FunctionalInterface
public interface TokenPredicate extends Predicate<Token> {
	public static final TokenPredicate EOF_PREDICATE = of(TokenKind.SPECIAL, JSSpecialGroup.EOF);
	public static final TokenPredicate EOL_PREDICATE = EOF_PREDICATE.or(of(TokenKind.SPECIAL, JSSpecialGroup.SEMICOLON));
	public static TokenPredicate of(TokenKind kind) {
		return t->(t.getKind()==kind);
	}
	public static TokenPredicate of(TokenKind kind, Object value) {
		return t->(t.getKind()==kind && t.getValue() == value);
	}
	public static TokenPredicate eof() {
		return EOF_PREDICATE;
	}
	default TokenPredicate or(TokenPredicate other) {
		return null;
	}
	default TokenPredicate and(TokenPredicate other) {
		return null;
	}
}
