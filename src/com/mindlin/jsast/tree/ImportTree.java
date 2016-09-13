package com.mindlin.jsast.tree;

import java.util.List;

public interface ImportTree extends StatementTree {
	List<ImportSpecifierTree> getSpecifiers();

	StringLiteralTree getSource();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IMPORT;
	}
}
