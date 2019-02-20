package com.mindlin.jsast.tree;

public interface ImportSpecifierTree extends Tree, UnvisitableTree {
	IdentifierTree getAlias();

	IdentifierTree getImported();

	boolean isDefault();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IMPORT_SPECIFIER;
	}
}
