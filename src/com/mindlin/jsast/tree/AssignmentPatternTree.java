package com.mindlin.jsast.tree;

public interface AssignmentPatternTree extends PatternTree {
	PatternTree getLeft();
	
	ExpressionTree getRight();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ASSIGNMENT_PATTERN;
	}
}
