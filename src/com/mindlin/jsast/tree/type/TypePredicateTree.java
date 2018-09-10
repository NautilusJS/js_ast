package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.ExpressionTree;

public interface TypePredicateTree extends TypeTree {
	ExpressionTree getParameterName();
}
