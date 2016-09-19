package com.mindlin.jsast.tree;

import java.util.List;

public interface VariableDeclarationTree extends StatementTree {
	List<VariableDeclaratorTree> getDeclarations();

	/**
	 * Whether this was initialized with a <code>let</code> keyword.
	 * 
	 * @return
	 */
	boolean isScoped();

	/**
	 * Whether this was initialized with a <code>const</code> keyword
	 * 
	 * @return
	 */
	boolean isConst();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VARIABLE_DECLARATION;
	}
}