package com.mindlin.jsast.tree;

public interface TypeTree extends IdentifierTree {
	boolean isFunction();
	boolean isIndex();
	boolean isArray();
	/**
	 * The variable or return type
	 * @return
	 */
	IdentifierTree getType();
}
