package com.mindlin.jsast.tree;

/**
 * Object literal assignment property, in form of 
 * {@code key: value}
 * 
 * @author mailmindlin
 */
public interface AssignmentPropertyTree extends PropertyTree, ObjectLiteralElement {
	ExpressionTree getValue();
	
	@Override
	default Kind getKind() {
		return Kind.ASSIGNMENT_PROPERTY;
	}
}
