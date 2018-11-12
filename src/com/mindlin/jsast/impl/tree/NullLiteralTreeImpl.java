package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.NullLiteralTree;

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
