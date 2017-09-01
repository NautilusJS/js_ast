package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeTree;

public interface CatchTree extends Tree {
	BlockTree getBlock();

	IdentifierTree getParameter();

	TypeTree getType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CATCH;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	default boolean equivalentTo(Tree otherTree) {
		//Hit the low hanging fruit first
		if (this == otherTree)
			return true;
		
		if (otherTree == null || this.getKind() != otherTree.getKind() || this.hashCode() != otherTree.hashCode())
			return false;
		
		
		CatchTree other = (CatchTree) otherTree;
		
		if (!Tree.equivalentTo(this.getBlock(), other.getBlock()))
			return false;
		
		if (!Tree.equivalentTo(this.getParameter(), other.getParameter()))
			return false;
		
		if (!Tree.equivalentTo(this.getType(), other.getType()))
			return false;
		
		return true;
	}
}
