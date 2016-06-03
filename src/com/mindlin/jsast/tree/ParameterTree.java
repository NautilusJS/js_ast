package com.mindlin.jsast.tree;

public interface Parameterree extends VariableTree {
	IdentifierTree getName();
	Optional<? extends ExpressionTree> getDefault();
	boolean isVarArgs();
	boolean isConst();
	Optional<IdentifierTree> getType();
}