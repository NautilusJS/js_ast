package com.mindlin.jsast.tree;

import java.util.List;

public interface SequenceTree extends ExpressionTree {
	List<ExpressionTree> getExpressions();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SEQUENCE;
	}
}
