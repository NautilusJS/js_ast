package com.mindlin.jsast.tree;

public interface DebuggerTree extends StatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.DEBUGGER;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitDebugger(this, data);
	}
}
