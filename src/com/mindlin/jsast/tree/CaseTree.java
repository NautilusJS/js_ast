package com.mindlin.jsast.tree;

import java.util.List;

public interface CaseTree extends StatementTree {
	ExpressionTree getExpression();
	List<? extends StatementTree> getStatements();
}
