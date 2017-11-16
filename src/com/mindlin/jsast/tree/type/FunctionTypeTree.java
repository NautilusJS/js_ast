package com.mindlin.jsast.tree.type;

import java.util.Collections;
import java.util.List;

import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.Tree;

/**
 * Represents a function type. May be written as
 * <code><...GENERICS>(...PARAMETERS) => RETURN_TYPE</code>.
 * 
 * @author mailmindlin
 */
public interface FunctionTypeTree extends TypeTree {
	/**
	 * @return the declared parameters. {@link Collections#emptyList()} should
	 *         be returned if no parameters are present.
	 */
	List<ParameterTree> getParameters();
	
	List<GenericParameterTree> getGenerics();
	
	TypeTree getReturnType();
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		
		if (other == null || this.getKind() != other.getKind() || !(other instanceof FunctionTypeTree)
				|| this.hashCode() != other.hashCode())
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
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitFunctionType(this, data);
	}
}
