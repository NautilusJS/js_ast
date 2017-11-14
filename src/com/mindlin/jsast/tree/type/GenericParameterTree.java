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
	 * <code>T extends Foo</code>, this method would return Foo.
	 * 
	 * @return supertype
	 */
	TypeTree getSupertype();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.GENERIC_PARAM;
	}
}
