package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.TypeTree;

public interface FunctionTypeTree extends TypeTree {
	List<ParameterTypeTree> getParameters();
	List<GenericTypeTree> getGenerics();
	TypeTree getReturnType();
}
