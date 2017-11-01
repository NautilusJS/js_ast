package com.mindlin.jsast.tree;

public interface DoWhileLoopTree extends ConditionalLoopTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.DO_WHILE_LOOP;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitDoWhileLoop(this, data);
	}
}