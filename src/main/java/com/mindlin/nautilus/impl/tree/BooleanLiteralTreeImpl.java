package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.BooleanLiteralTree;

public class BooleanLiteralTreeImpl extends AbstractTree implements BooleanLiteralTree {
	protected final boolean value;
	public BooleanLiteralTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), t.getValue());
	}
	public BooleanLiteralTreeImpl(SourcePosition start, SourcePosition end, boolean value) {
		super(start, end);
		this.value = value;
	}

	@Override
	public Boolean getValue() {
		return value;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getValue());
	}
}

