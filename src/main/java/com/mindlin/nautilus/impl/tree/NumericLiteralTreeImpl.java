package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.NumericLiteralTree;

public class NumericLiteralTreeImpl extends AbstractTree implements NumericLiteralTree {
	protected final Number value;
	
	public NumericLiteralTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), t.getValue());
	}
	
	public NumericLiteralTreeImpl(SourcePosition start, SourcePosition end, Number value) {
		super(start, end);
		this.value = value;
	}
	
	@Override
	public Number getValue() {
		return value;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getValue());
	}
}
