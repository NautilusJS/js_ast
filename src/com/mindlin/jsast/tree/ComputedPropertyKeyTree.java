package com.mindlin.jsast.tree;

public interface ComputedPropertyKeyTree extends PropertyName, UnvisitableTree {
	
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Kind.OBJECT_LITERAL_PROPERTY;
	}
}
