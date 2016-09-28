package com.mindlin.jsast.tree;

import java.util.List;

public interface ArrayLiteralTree extends ExpressionTree {
	List<? extends ExpressionTree> getElements();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ARRAY_LITERAL;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitArrayLiteral(this, data);
	}
}
