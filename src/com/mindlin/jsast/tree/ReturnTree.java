package com.mindlin.jsast.tree;

public interface ReturnTree extends StatementTree {
	
	/**
	 * @return return expression (null if not present)
	 */
	ExpressionTree getExpression();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.RETURN;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitReturn(this, data);
	}
}
