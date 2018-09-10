package com.mindlin.jsast.tree;

public interface FunctionExpressionTree extends FunctionTree, ExpressionTree {
	
	@Override
	default Kind getKind() {
		return Tree.Kind.FUNCTION_EXPRESSION;
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitFunctionExpression(this, data);
	}
}
