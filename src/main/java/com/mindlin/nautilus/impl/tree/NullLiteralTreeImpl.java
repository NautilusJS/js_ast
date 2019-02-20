package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.NullLiteralTree;

public class NullLiteralTreeImpl extends AbstractTree implements NullLiteralTree {
	public NullLiteralTreeImpl(Token t) {
		this(t.getStart(), t.getEnd());
	}
	
	public NullLiteralTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
	
	@Override
	public Void getValue() {
		return null;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getValue());
	}
	
}
