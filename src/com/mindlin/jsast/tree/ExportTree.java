package com.mindlin.jsast.tree;

public interface ExportTree extends ExpressiveStatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.EXPORT;
	}
}
