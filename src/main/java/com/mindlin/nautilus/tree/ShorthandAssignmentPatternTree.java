package com.mindlin.jsast.tree;

public interface ShorthandAssignmentPatternTree extends AssignmentPatternTree {
	@Override
	IdentifierTree getName();
	
	@Override
	default IdentifierTree getValue() {
		return getName();
	}
	
	@Override
	default Kind getKind() {
		return Kind.SHORTHAND_ASSIGNMENT_PATTERN;
	}
}
