package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.StringLiteralTree;

public class StringLiteralTreeImpl extends AbstractTree implements StringLiteralTree {
	protected final String value;
	
	public StringLiteralTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), t.getValue());
	}
	
	public StringLiteralTreeImpl(long start, long end, String value) {
		super(start, end);
		this.value = value;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getValue());
	}
}
