package com.mindlin.jsast.tree;

import java.util.List;

public interface BlockTree extends StatementTree {
	List<? extends StatementTree> getStatements();

	boolean isScoped();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.BLOCK;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitBlock(this, data);
	}

	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;

		List<? extends StatementTree> statements = this.getStatements();
		if (other.getKind() == Kind.BLOCK && Tree.equivalentTo(this.getStatements(), ((BlockTree)other).getStatements()))
			return true;
		
		if (statements.size() == 1 && statements.get(0).equivalentTo(other))
			return true;
		
		return false;
	}
}