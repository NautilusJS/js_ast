package com.mindlin.jsast.tree;

public interface IfTree extends StatementTree {
	/**
	 * Condition that is tested in the if tree
	 * 
	 * @return condition
	 */
	ExpressionTree getExpression();
	
	/**
	 * Statement that is executed if {@link #getExpression()} evaluates to true.
	 * 
	 * @return then
	 */
	StatementTree getThenStatement();
	
	/**
	 * Statement that is executed if {@link #getExpression()} does not evaluate
	 * to true. May not be present.
	 * 
	 * @return else statement; null if not present
	 */
	StatementTree getElseStatement();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.IF;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitIf(this, data);
	}
}
