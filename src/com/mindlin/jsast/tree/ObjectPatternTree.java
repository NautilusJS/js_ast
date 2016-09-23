package com.mindlin.jsast.tree;

import java.util.List;

public interface ObjectPatternTree extends PatternTree {
	List<ObjectPatternPropertyTree> getProperties();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_PATTERN;
	}
}
