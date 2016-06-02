package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.UnaryTree;

public class UnaryOperatorTreeImpl extends AbstractExpressiveExpressionTreeImpl implements UnaryTree {
	protected final Kind kind;
	protected UnaryOperatorTreeImpl(long start, long end, ExpressionTree expression, Kind kind) {
		super(start, end, expression);
		this.kind = kind;
	}

	@Override
	public Kind getKind() {
		return this.kind;
	}
}
