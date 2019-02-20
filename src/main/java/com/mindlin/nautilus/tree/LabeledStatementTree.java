package com.mindlin.jsast.tree;

public interface LabeledStatementTree extends StatementTree {
	IdentifierTree getName();
	
	StatementTree getStatement();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.LABELED_STATEMENT;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitLabeledStatement(this, data);
	}
}
