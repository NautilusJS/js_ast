package com.mindlin.nautilus.tree;

import com.mindlin.nautilus.tree.ObjectPatternTree.ObjectPatternElement;

public interface AssignmentPatternTree extends PropertyTree, ObjectPatternElement, UnvisitableTree {
	PatternTree getValue();
	
	ExpressionTree getInitializer();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ASSIGNMENT_PATTERN;
	}
}
