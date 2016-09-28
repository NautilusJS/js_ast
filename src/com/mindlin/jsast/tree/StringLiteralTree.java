package com.mindlin.jsast.tree;

public interface StringLiteralTree extends LiteralTree<String>, ObjectPropertyKeyTree {
	
	@Override
	default boolean isComputed() {
		return false;
	}
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.STRING_LITERAL;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitStringLiteral(this, data);
	}
}
