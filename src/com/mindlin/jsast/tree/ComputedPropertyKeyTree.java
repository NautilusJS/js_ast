package com.mindlin.jsast.tree;

public interface ComputedPropertyKeyTree extends DeclarationName, UnvisitableTree {
	
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Kind.OBJECT_LITERAL_PROPERTY;
	}
}
