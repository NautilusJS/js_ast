package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.StringLiteralTree;

public class StringLiteralTreeImpl extends AbstractTree implements StringLiteralTree {
	protected final String value;
	
	public StringLiteralTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), t.getValue());
	}
	
	public StringLiteralTreeImpl(SourcePosition start, SourcePosition end, String value) {
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
