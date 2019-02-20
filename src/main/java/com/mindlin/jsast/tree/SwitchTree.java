package com.mindlin.jsast.tree;

import java.util.List;

public interface SwitchTree extends StatementTree {
	List<? extends SwitchCaseTree> getCases();

	ExpressionTree getExpression();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SWITCH;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitSwitch(this, data);
	}
}
