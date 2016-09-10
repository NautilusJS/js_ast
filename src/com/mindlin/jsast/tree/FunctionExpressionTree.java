package com.mindlin.jsast.tree;

import java.util.List;

public interface FunctionExpressionTree extends ExpressionTree {
	BlockTree getBody();

	String getName();

	List<? extends ExpressionTree> getParameters();

	boolean isStrict();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_EXPRESSION;
	}
}
