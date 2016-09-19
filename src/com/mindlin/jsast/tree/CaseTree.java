package com.mindlin.jsast.tree;

import java.util.List;

public interface CaseTree extends StatementTree {
	ExpressionTree getExpression();

	List<? extends StatementTree> getStatements();

	boolean isDefault();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CASE;
	}
}
