package com.mindlin.jsast.tree;

public interface ImportSpecifierTree extends Tree {
	IdentifierTree getAlias();

	IdentifierTree getImported();

	boolean isDefault();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IMPORT_SPECIFIER;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
