package com.mindlin.jsast.tree;

import java.util.List;

public interface SwitchCaseTree extends UnvisitableTree {
	ExpressionTree getExpression();
	
	default boolean isDefault() {
		return getExpression() == null;
	}
	
	List<? extends StatementTree> getBody();
	
	@Override
	default Kind getKind() {
		return Kind.CASE;
	}
}