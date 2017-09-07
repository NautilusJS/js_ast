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
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitBlock(this, data);
	}

	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;

		List<? extends StatementTree> statements = this.getStatements();
		if (other.getKind() == Kind.BLOCK) {
			List<? extends StatementTree> otherStmts = ((BlockTree)other).getStatements();
			if (statements.size() == otherStmts.size()) {
				boolean eq = true;
				for (int i = 0; i < statements.size() && eq; i++) {
					StatementTree a = statements.get(i);
					StatementTree b = otherStmts.get(i);
					eq &= a.equivalentTo(b) || b.equivalentTo(a);
				}
				if (eq)
					return true;
			}
		}
		
		if (statements.size() == 1 && statements.get(0).equivalentTo(other))
			return true;
		
		return false;
	}
}