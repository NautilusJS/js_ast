package com.mindlin.jsast.tree;

public interface ImportTree extends StatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IMPORT;
	}
}
