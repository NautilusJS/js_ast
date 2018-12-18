package com.mindlin.jsast.tree;

import java.util.List;

public interface ImportDeclarationTree extends StatementTree {
	List<ImportSpecifierTree> getSpecifiers();

	StringLiteralTree getSource();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IMPORT;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitImport(this, data);
	}
}
