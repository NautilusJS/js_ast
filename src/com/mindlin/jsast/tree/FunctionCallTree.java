package com.mindlin.jsast.tree;

import java.util.List;

public interface FunctionCallTree extends ExpressionTree {
	List<? extends ExpressionTree> getArguments();
	ExpressionTree getFunctionSelect();
}
