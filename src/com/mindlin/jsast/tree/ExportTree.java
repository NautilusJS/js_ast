package com.mindlin.jsast.tree;

public interface ExportTree extends StatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.EXPORT;
	}
}
