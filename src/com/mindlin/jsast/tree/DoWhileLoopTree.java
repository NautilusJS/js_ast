package com.mindlin.jsast.tree;
public interface DoWhileLoopTree extends ConditionalLoopTree {
	default Tree.Kind getKind() {
		return Tree.Kind.DO_WHILE_LOOP;
	}
}