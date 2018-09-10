package com.mindlin.jsast.tree;

public interface NamedDeclarationTree extends DeclarationTree {
	/**
	 * Get declaration name
	 * @return declaration name
	 */
	DeclarationName getName();
}
