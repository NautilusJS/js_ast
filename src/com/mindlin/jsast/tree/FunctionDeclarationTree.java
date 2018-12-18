package com.mindlin.jsast.tree;

public interface FunctionDeclarationTree extends DecoratableTree, FunctionTree, DeclarationStatementTree {
	/**
	 * {@inheritDoc}
	 * <br/>
	 * Must not be null.
	 */
	@Override
	IdentifierTree getName();
	
	@Override
	default boolean isArrow() {
		return false;
	}
	
	@Override
	default Kind getKind() {
		return Tree.Kind.FUNCTION_DECLARATION;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitFunctionDeclaration(this, data);
	}
}
