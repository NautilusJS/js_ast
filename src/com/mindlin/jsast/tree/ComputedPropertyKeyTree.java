package com.mindlin.jsast.tree;

public interface ComputedPropertyKeyTree extends PropertyName, UnvisitableTree {
	
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Kind.COMPUTED_PROPERTY_KEY;
	}
}
