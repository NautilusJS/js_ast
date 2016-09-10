package com.mindlin.jsast.tree;

import java.util.Optional;

public interface VariableTree extends StatementTree {
	IdentifierTree getName();

	/**
	 * Get any initializer for this variable. For parameters, this is the
	 * default value. An empty Optional means that there was no
	 * initializer/default value.
	 * 
	 * @return
	 */
	Optional<? extends ExpressionTree> getInitializer();

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

	/**
	 * Get the type of this variable. Return null for any.
	 * 
	 * @return
	 */
	TypeTree getType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VARIABLE;
	}
}