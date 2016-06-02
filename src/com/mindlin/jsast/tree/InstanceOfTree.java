package com.mindlin.jsast.tree;

public interface InstanceOfTree extends UnaryTree, ExpressionTree {
	Tree getType();
}