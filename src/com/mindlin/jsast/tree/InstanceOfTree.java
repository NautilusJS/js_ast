package com.mindlin.jsast.tree;

public interface InstanceOfTree extends UnaryTree, ExpressionTree {
	Tree getType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.INSTANCE_OF;
	}
}