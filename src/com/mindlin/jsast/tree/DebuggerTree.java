package com.mindlin.jsast.tree;

public interface DebuggerTree extends StatementTree {
	default Tree.Kind getKind() {
		return Tree.Kind.DEBUGGER;
	}
}
