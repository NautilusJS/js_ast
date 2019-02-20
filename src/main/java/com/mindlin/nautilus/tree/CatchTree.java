package com.mindlin.jsast.tree;

public interface CatchTree extends Tree, UnvisitableTree {
	/**
	 * Get declared parameter.
	 * @return declared parameter (may be null)
	 */
	VariableDeclaratorTree getParameter();
	
	BlockTree getBlock();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CATCH;
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
		
		return true;
	}
}
