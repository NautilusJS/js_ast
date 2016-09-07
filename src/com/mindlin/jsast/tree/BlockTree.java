package com.mindlin.jsast.tree;

import java.util.List;

public interface BlockTree extends StatementTree {
	List<? extends StatementTree> getStatements();
	boolean isScoped();
	default Tree.Kind getKind() {
		return Tree.Kind.BLOCK;
	}
}