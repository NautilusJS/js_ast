package com.mindlin.jsast.tree;

public interface DebuggerTree extends StatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.DEBUGGER;
	}
}
