package com.mindlin.jsast.tree;

public interface TypeTree extends Tree {
	boolean isOptional();
	boolean isFunction();
	boolean isIndex();
	/**
	 * The variable or return type
	 * @return
	 */
	IdentifierTree getType();
}
