package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.ObjectPatternTree.ObjectPatternElement;

public interface RestPatternElementTree extends PatternTree, ObjectPatternElement {
	PatternTree getValue();
	
	@Override
	default Kind getKind() {
		return Kind.REST_PATTERN;
	}
}
