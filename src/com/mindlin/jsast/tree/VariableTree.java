package com.mindlin.jsast.tree;

public interface VariableTree extends StatementTree {
	IdentifierTree getName();
	Optional<? extends ExpressionTree> getInitializer();
	boolean isScoped();
	boolean isConst();
	Optional<IdentifierTree> getType();
}