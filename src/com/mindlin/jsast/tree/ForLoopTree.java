package com.mindlin.jsast.tree;

public interface ForLoopTree extends LoopTree {
	StatementTree getInitializer();
	
	ExpressionTree getCondition();
	
	ExpressionTree getUpdate();

	default Tree.Kind getKind() {
		return Tree.Kind.FOR_LOOP;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitForLoop(this, data);
	}
	
	@Override
	default boolean equivalentTo(Tree ot) {
		if (this == ot)
			return true;
		
		if (ot == null || this.getKind() != ot.getKind() || this.hashCode() != ot.hashCode())
			return false;
		
		
		ForLoopTree other = (ForLoopTree) ot;
		
		if (!Tree.equivalentTo(this.getInitializer(), other.getInitializer()))
			return false;
		
		if (!Tree.equivalentTo(this.getCondition(), other.getCondition()))
			return false;
		
		if (!Tree.equivalentTo(this.getUpdate(), other.getUpdate()))
			return false;
		
		if (!Tree.equivalentTo(this.getStatement(), other.getStatement()))
			return false;
		
		return true;
	}
}