package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.Tree;

/**
 * 
 * @author mailmindlin
 */
public interface FunctionTypeTree extends TypeTree {
	List<ParameterTree> getParameters();

	List<GenericTypeTree> getGenerics();

	TypeTree getReturnType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_TYPE;
	}
}
