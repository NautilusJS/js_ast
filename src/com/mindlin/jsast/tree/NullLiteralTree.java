package com.mindlin.jsast.tree;

public interface NullLiteralTree extends LiteralTree<Void> {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.NULL_LITERAL;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitNull(this, data);
	}
}
