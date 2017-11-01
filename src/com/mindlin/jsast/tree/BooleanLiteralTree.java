package com.mindlin.jsast.tree;

public interface BooleanLiteralTree extends LiteralTree<Boolean> {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.BOOLEAN_LITERAL;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitBooleanLiteral(this, data);
	}
}
