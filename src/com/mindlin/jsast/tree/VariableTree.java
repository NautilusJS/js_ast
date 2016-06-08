package com.mindlin.jsast.tree;

import java.util.Optional;

public interface VariableTree extends StatementTree {
	IdentifierTree getName();
	Optional<? extends ExpressionTree> getInitializer();
	boolean isScoped();
	boolean isConst();
	Optional<TypeTree> getType();
}