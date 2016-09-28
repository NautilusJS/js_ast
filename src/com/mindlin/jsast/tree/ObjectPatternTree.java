package com.mindlin.jsast.tree;

import java.util.List;

public interface ObjectPatternTree extends PatternTree {
	List<ObjectPatternPropertyTree> getProperties();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_PATTERN;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitObjectPattern(this, data);
	}
}
