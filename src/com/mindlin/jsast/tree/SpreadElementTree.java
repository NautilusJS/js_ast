package com.mindlin.jsast.tree;

public interface SpreadElementTree extends ExpressionTree, ObjectLiteralElement {
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Tree.Kind.SPREAD;
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitSpread(this, data);
	}
}
