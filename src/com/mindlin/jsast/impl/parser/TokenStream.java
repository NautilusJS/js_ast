package com.mindlin.jsast.impl.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class TokenStream {
	List<Token> tokens = new LinkedList<>();
	ListIterator<Token> iterator;
	JSLexer lexer;
	boolean isEOF = false;
	public TokenStream(JSLexer lexer) {
		this.lexer = lexer;
		this.iterator = tokens.listIterator();
	}
	public Token current() {
		iterator.
	}
	public Token next() {
		
	}
	public Token prev() {
		
	}
	public void skip(int offset) {
		this.index += index;
	}
	public void seek(int position) {
		this.index = position;
	}
	public int getPosition() {
		return this.index;
	}
}
