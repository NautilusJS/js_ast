package com.mindlin.jsast.tree;
public interface WhileLoopTree extends ConditionalLoopTree {
	default Tree.Kind getKind() {
		return Tree.Kind.WHILE_LOOP;
	}
}