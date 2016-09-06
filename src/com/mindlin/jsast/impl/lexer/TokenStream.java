package com.mindlin.jsast.impl.lexer;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class TokenStream {
	List<Token> tokens = new LinkedList<>();
	ListIterator<Token> iterator;
	JSLexer lexer;
	boolean isEOF = false;
	int index;
	public TokenStream(JSLexer lexer) {
		this.lexer = lexer;
		this.iterator = tokens.listIterator();
	}
	public Token current() {
		throw new UnsupportedOperationException();
	}
	public Token next() {
		throw new UnsupportedOperationException();
	}
	public Token prev() {
		throw new UnsupportedOperationException();
	}
	public void skip(int offset) {
		this.index += offset;
	}
	public void seek(int position) {
		this.index = position;
	}
	public int getPosition() {
		return this.index;
	}
}
