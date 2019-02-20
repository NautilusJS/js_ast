package com.mindlin.jsast.tree;

import java.util.List;

public interface ObjectLiteralTree extends ExpressionTree {
	List<? extends ObjectLiteralElement> getProperties();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_LITERAL;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitObjectLiteral(this, data);
	}
}