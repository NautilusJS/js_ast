package com.mindlin.nautilus.tree;

import java.util.List;

import com.mindlin.nautilus.tree.type.TypeTree;

public interface NewTree extends ExpressionTree {
	ExpressionTree getCallee();
	
	List<TypeTree> getTypeArguments();

	List<ExpressionTree> getArguments();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.NEW;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitNew(this, data);
	}
}