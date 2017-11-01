package com.mindlin.jsast.tree;

public interface StatementTree extends Tree {
	<R, D> R accept(StatementTreeVisitor<R, D> visitor, D data);
}
