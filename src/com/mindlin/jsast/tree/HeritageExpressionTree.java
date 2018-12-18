package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.TypeTree;

public interface HeritageExpressionTree extends Tree, UnvisitableTree {
	ExpressionTree getExpression();
	List<TypeTree> getTypeAguments();
	
	@Override
	default Kind getKind() {
		return Kind.HERITAGE_EXPRESSION;
	}
}
