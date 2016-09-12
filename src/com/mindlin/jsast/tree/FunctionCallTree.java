package com.mindlin.jsast.tree;

import java.util.List;

public interface FunctionCallTree extends StatementTree, ExpressionTree {
	List<? extends ExpressionTree> getArguments();

	ExpressionTree getFunctionSelect();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_INVOCATION;
	}
}
