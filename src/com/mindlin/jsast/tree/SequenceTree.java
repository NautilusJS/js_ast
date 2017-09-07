package com.mindlin.jsast.tree;

import java.util.List;

public interface SequenceTree extends ExpressionTree {
	List<ExpressionTree> getExpressions();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SEQUENCE;
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		return this == other;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitSequence(this, data);
	}
}
