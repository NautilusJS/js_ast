package com.mindlin.jsast.tree;

public interface BreakTree extends GotoTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.BREAK;
	}
}
