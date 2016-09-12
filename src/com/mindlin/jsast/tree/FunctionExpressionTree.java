package com.mindlin.jsast.tree;

import java.util.List;

public interface FunctionExpressionTree extends ExpressionTree {
	StatementTree getBody();

	String getName();

	List<ParameterTree> getParameters();

	boolean isStrict();
	
	boolean isArrow();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_EXPRESSION;
	}
}
