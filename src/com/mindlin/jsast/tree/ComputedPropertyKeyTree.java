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
}
