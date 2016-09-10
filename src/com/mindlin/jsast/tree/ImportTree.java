package com.mindlin.jsast.tree;

import java.util.Map;

public interface ImportTree extends StatementTree {
	public static final String WILDCARD = "*";

	Map<String, String> getImports();

	boolean importDefault();

	String getTarget();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IMPORT;
	}
}
