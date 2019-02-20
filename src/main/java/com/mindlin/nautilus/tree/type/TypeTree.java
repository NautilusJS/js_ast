package com.mindlin.nautilus.tree.type;

import com.mindlin.nautilus.tree.Tree;
import com.mindlin.nautilus.tree.TreeVisitor;

public interface TypeTree extends Tree {
	
	<R, D> R accept(TypeTreeVisitor<R, D> visitor, D data);
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((TypeTreeVisitor<R, D>) visitor, data);
	}
}
