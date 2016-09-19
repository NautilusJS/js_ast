package com.mindlin.jsast.tree;

public interface ForLoopTree extends ConditionalLoopTree {
	StatementTree getInitializer();

	ExpressionTree getUpdate();

	default Tree.Kind getKind() {
		return Tree.Kind.FOR_LOOP;
	}
}