package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.SignatureDeclarationTree;
import com.mindlin.jsast.tree.Tree;

/**
 * Represents a function type. May be written as
 * {@code <...GENERICS>(...PARAMETERS) => RETURN_TYPE}.
 * 
 * @author mailmindlin
 */
public interface FunctionTypeTree extends TypeTree, SignatureDeclarationTree {
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		
		if (other == null || this.getKind() != other.getKind() || !(other instanceof FunctionTypeTree)
				|| this.hashCode() != other.hashCode())
			return false;
		
		FunctionTypeTree o = (FunctionTypeTree) other;
		
		return Tree.equivalentTo(this.getParameters(), o.getParameters())
				&& Tree.equivalentTo(this.getTypeParameters(), o.getTypeParameters())
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
