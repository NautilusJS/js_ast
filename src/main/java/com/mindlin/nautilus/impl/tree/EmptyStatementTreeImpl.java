package com.mindlin.nautilus.impl.tree;

import java.util.Objects;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.EmptyStatementTree;

public class EmptyStatementTreeImpl extends AbstractTree implements EmptyStatementTree {

	public EmptyStatementTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}

	public EmptyStatementTreeImpl(Token t) {
		this(t.getStart(), t.getEnd());
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind());
	}
}
