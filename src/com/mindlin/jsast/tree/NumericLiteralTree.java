package com.mindlin.jsast.tree;

public interface NumericLiteralTree extends LiteralTree<Number>, ObjectPropertyKeyTree {
	
	@Override
	default boolean isComputed() {
		return false;
	}
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.NUMERIC_LITERAL;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitNumericLiteral(this, data);
	}
}
