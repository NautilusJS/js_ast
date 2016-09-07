package com.mindlin.jsast.tree;

public interface ParameterTree extends VariableTree {
	boolean isVarArgs();
	boolean isOptional();
}