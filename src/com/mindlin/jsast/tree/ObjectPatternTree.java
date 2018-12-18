package com.mindlin.jsast.tree;

import java.util.List;

public interface ObjectPatternTree extends PatternTree {
	List<ObjectPatternElement> getProperties();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_PATTERN;
	}

	@Override
	default <R, D> R accept(PatternTreeVisitor<R, D> visitor, D data) {
		return visitor.visitObjectPattern(this, data);
	}
	
	public interface ObjectPatternElement extends DeclarationTree {
		
	}
}
