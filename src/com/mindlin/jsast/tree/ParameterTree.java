package com.mindlin.jsast.tree;

public interface ParameterTree extends VariableTree {
	boolean isVarArgs();

	boolean isOptional();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.PARAMETER;
	}
}