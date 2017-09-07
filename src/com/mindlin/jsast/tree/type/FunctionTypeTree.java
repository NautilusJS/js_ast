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
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		
		if (other == null || this.getKind() != other.getKind() || !(other instanceof FunctionTypeTree) || this.hashCode() != other.hashCode())
			return false;
		
		
		FunctionTypeTree o = (FunctionTypeTree) other;
		
		return Tree.equivalentTo(this.getParameters(), o.getParameters())
				&& Tree.equivalentTo(this.getGenerics(), o.getGenerics())
				&& Tree.equivalentTo(this.getReturnType(), o.getReturnType());
	}

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.FUNCTION_TYPE;
	}
}
