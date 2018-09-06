package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Tree;

/**
 * Generic type parameter (not argument).
 * 
 * @author mailmindlin
 * @see GenericRefTypeTree
 */
public interface GenericParameterTree extends TypeTree {
	
	/**
	 * Name of generic parameter.
	 * 
	 * @return name (not null)
	 */
	IdentifierTree getName();
	
	/**
	 * Supertype of generic parameter. For example, if declared as
	 * {@code T extends Foo}, this method would return Foo.
	 * 
	 * @return supertype, else null if not present
	 */
	TypeTree getSupertype();
	
	/**
	 * Default value of parameter
	 * @return default value, else null if not present
	 */
	TypeTree getDefault();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.GENERIC_PARAM;
	}
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
