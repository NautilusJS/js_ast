package com.mindlin.jsast.tree;

import java.util.List;

public interface FunctionCallTree extends ExpressionTree {
	List<? extends ExpressionTree> getArguments();
	
	ExpressionTree getCallee();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_INVOCATION;
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitFunctionCall(this, data);
	}
}
