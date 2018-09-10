package com.mindlin.jsast.tree;

public interface ComputedPropertyKeyTree extends DeclarationName {
	
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Kind.OBJECT_LITERAL_PROPERTY;
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
