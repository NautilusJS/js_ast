package com.mindlin.jsast.tree;

public interface ThrowTree extends StatementTree {
	ExpressionTree getExpression();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.THROW;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitThrow(this, data);
	}
}
