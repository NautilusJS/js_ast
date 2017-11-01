package com.mindlin.jsast.tree;

public interface ObjectPatternPropertyTree extends ObjectPropertyTree, PatternTree {
	PatternTree getValue();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_PATTERN;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
