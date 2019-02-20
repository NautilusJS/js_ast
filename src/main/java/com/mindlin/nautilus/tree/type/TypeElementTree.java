package com.mindlin.nautilus.tree.type;

import com.mindlin.nautilus.tree.DeclarationTree;
import com.mindlin.nautilus.tree.TreeVisitor;
import com.mindlin.nautilus.tree.TypeElementVisitor;

public interface TypeElementTree extends DeclarationTree {
	<R, D> R accept(TypeElementVisitor<R, D> visitor, D data);
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((TypeElementVisitor<R, D>) visitor, data);
	}
}
