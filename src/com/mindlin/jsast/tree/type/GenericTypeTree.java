package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Tree;

/**
 * Represents a type placeholder for a generic type. Example: for the type
 * <code>Array<T></code>, T would be a generic type.
 * 
 * TODO: maybe rename to generic declaration?
 * @see GenericRefTypeTree
 * @author mailmindlin
 */
public interface GenericTypeTree extends TypeTree {
	
	IdentifierTree getName();
	
	TypeTree getSupertype();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.GENERIC_PARAM;
	}
}
