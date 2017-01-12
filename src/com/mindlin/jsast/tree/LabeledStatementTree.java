package com.mindlin.jsast.tree;

public interface LabeledStatementTree extends ControlStatementTree {
	IdentifierTree getName();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.LABELED_STATEMENT;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitLabeledStatement(this, data);
	}
}
