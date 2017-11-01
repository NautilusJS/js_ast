package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeTree;

public interface CastTree extends ExpressiveExpressionTree {
	/**
	 * Get the type that the expression was cast to
	 * @return
	 */
	TypeTree getType();

	@Override
	default Kind getKind() {
		return Kind.CAST;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitCast(this, data);
	}
	
}
