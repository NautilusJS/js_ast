package com.mindlin.jsast.tree;

import java.util.List;

public interface ArrayPatternTree extends PatternTree {
	List<PatternTree> getElements();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ARRAY_PATTERN;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitArrayPattern(this, data);
	}
}
