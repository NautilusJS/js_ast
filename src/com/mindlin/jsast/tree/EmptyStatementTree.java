package com.mindlin.jsast.tree;

public interface EmptyStatementTree extends StatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.EMPTY_STATEMENT;
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		return other != null
				&& getKind() == other.getKind()
				&& (other instanceof EmptyStatementTree);
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitEmptyStatement(this, data);
	}
}
