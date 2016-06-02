package com.mindlin.jsast.tree;

import java.util.List;

public interface SwitchTree extends StatementTree {
	List<? extends CaseTree> getCases();
	ExpressionTree getExpression();
	default Tree.Kind getKind() {
		return Tree.Kind.SWITCH;
	}
}
