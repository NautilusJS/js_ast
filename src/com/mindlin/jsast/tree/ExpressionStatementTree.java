package com.mindlin.jsast.tree;

public interface ExpressionStatementTree extends StatementTree {
	ExpressionTree getExpression();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.EXPRESSION_STATEMENT;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitExpressionStatement(this, data);
	}
}
