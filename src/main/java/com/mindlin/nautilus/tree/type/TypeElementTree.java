package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.DeclarationTree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TypeElementVisitor;

public interface TypeElementTree extends DeclarationTree {
	<R, D> R accept(TypeElementVisitor<R, D> visitor, D data);
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((TypeElementVisitor<R, D>) visitor, data);
	}
}
