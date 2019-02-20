package com.mindlin.jsast.tree;

public interface StringLiteralTree extends LiteralTree<String>, PropertyName {
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.STRING_LITERAL;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitStringLiteral(this, data);
	}
}
