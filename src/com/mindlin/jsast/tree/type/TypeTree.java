package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

public interface TypeTree extends Tree {
	/**
	 * Returns whether the current node refers to an implicit type.
	 * @return if implicit type
	 */
	boolean isImplicit();
	
	<R, D> R accept(TypeTreeVisitor<R, D> visitor, D data);
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((TypeTreeVisitor<R, D>) visitor, data);
	}
}
