package com.mindlin.jsast.tree;

import java.util.List;

public interface VariableDeclarationTree extends StatementTree, PatternTree {
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
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitVariableDeclaration(this, data);
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitVariableDeclaration(this, data);
	}
	
	@Override
	default <R, D> R accept(PatternTreeVisitor<R, D> visitor, D data) {
		return visitor.visitVariableDeclaration(this, data);
	}
}