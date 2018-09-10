package com.mindlin.jsast.tree;

public interface AssignmentPropertyTree extends PropertyTree, ObjectLiteralElement {
	ExpressionTree getValue();
	
	@Override
	default Kind getKind() {
		return Kind.ASSIGNMENT_PROPERTY;
	}
}
