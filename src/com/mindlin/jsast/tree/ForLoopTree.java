package com.mindlin.jsast.tree;

public interface ForLoopTree extends ConditionalLoopTree {
	StatementTree getInitializer();

	ExpressionTree getUpdate();

	default Tree.Kind getKind() {
		return Tree.Kind.FOR_LOOP;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitForLoop(this, data);
	}
}