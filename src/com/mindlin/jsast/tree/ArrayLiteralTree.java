package com.mindlin.jsast.tree;

import java.util.List;

public interface ArrayLiteralTree extends ExpressionTree {
	List<? extends ExpressionTree> getElements();
}
