package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.ComputedPropertyKeyTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

public interface EnumMemberTree extends Tree {
	/**
	 * Get name of member.
	 * May not be {@link ComputedPropertyKeyTree}.
	 * @return name
	 */
	ObjectPropertyKeyTree getName();
	
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
