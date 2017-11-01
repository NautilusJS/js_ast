package com.mindlin.jsast.tree;

import java.util.List;

public interface NewTree extends ExpressionTree {
	ExpressionTree getCallee();

	List<ExpressionTree> getArguments();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.NEW;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitNew(this, data);
	}
}