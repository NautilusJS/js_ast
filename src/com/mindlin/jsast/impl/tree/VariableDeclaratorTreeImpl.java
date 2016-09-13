package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;

public class VariableDeclaratorTreeImpl extends IdentifierTreeImpl implements VariableDeclaratorTree {
	protected final ExpressionTree initializer;
	protected final TypeTree type;

	public VariableDeclaratorTreeImpl(long start, long end, String name, TypeTree type, ExpressionTree initializer) {
		super(start, end, name);
		this.type = type;
		this.initializer = initializer;
	}

	@Override
	public ExpressionTree getIntitializer() {
		return this.initializer;
	}

	@Override
	public TypeTree getType() {
		return this.type;
	}

}
