package com.mindlin.nautilus.tree;

import com.mindlin.nautilus.tree.ObjectPatternTree.ObjectPatternElement;

public interface RestPatternElementTree extends PatternTree, ObjectPatternElement {
	PatternTree getValue();
	
	@Override
	default Kind getKind() {
		return Kind.REST_PATTERN;
	}
}
