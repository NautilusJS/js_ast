package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.NamedDeclarationTree;
import com.mindlin.jsast.tree.TreeVisitor;

public interface EnumMemberTree extends NamedDeclarationTree {
	
	/**
	 * (Optional) initializer.
	 * @return initializer
	 */
	ExpressionTree getInitializer();
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
