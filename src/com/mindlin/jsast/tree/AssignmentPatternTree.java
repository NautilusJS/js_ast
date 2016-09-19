package com.mindlin.jsast.tree;

public interface AssignmentPatternTree extends PatternTree {
	PatternTree getLeft();
	ExpressionTree getRight();
}
