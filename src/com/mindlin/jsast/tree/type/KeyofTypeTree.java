package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

/**
 * Type representing a type expression in the form of {@code keyof X}
 * @author mailmindlin
 */
public interface KeyofTypeTree extends TypeTree {
	
	/**
	 * Get the type that this is a key of ({@code X} in {@code keyof X}).
	 * @return base type
	 */
	TypeTree getBaseType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.KEYOF_TYPE;
	}
}
