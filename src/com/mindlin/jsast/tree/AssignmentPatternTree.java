package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.ObjectPatternTree.ObjectPatternElement;

public interface AssignmentPatternTree extends PropertyTree, ObjectPatternElement {
	PatternTree getValue();
	
	ExpressionTree getInitializer();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ASSIGNMENT_PATTERN;
	}
}
