package com.mindlin.jsast.tree;

public interface ComputedPropertyKeyTree extends ExpressiveExpressionTree, ObjectPropertyKeyTree {
	
	@Override
	default boolean isComputed() {
		return true;
	}
	
	@Override
	default Kind getKind() {
		return Kind.OBJECT_LITERAL_PROPERTY;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitComputedPropertyKey(this, data);
	}
}
