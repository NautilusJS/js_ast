package com.mindlin.nautilus.tree;

import java.util.List;

import com.mindlin.nautilus.tree.type.TypeTree;

public interface HeritageExpressionTree extends Tree, UnvisitableTree {
	ExpressionTree getExpression();
	List<TypeTree> getTypeAguments();
	
	@Override
	default Kind getKind() {
		return Kind.HERITAGE_EXPRESSION;
	}
}
