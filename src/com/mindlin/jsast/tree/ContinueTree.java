package com.mindlin.jsast.tree;

public interface ContinueTree extends GotoTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CONTINUE;
	}
}
