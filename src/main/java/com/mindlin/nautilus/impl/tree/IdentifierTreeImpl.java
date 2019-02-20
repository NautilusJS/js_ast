package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.IdentifierTree;

public class IdentifierTreeImpl extends AbstractTree implements IdentifierTree {
	protected final String name;
	
	public IdentifierTreeImpl(SourcePosition start, SourcePosition end, String name) {
		super(start, end);
		this.name = name;
	}
	
	public IdentifierTreeImpl(Token token) {
		this(token.getStart(), token.getEnd(), token.getValue().toString());
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName());
	}
}
