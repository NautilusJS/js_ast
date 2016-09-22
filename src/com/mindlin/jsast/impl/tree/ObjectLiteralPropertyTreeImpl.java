package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.ObjectLiteralPropertyTree;

public class ObjectLiteralPropertyTreeImpl extends AbstractTree implements ObjectLiteralPropertyTree {
	protected final IdentifierTree name;
	protected final ExpressionTree value;
	public ObjectLiteralPropertyTreeImpl(long start, long end, IdentifierTree name, ExpressionTree value) {
		super(start, end);
		this.name = name;
		this.value = value;
	}

	@Override
	public IdentifierTree getKey() {
		return name;
	}
	
	@Override
	public ExpressionTree getValue() {
		return value;
	}
	
}
