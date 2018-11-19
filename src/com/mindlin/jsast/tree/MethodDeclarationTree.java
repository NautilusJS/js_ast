package com.mindlin.jsast.tree;

/**
 * Object literal/class method declaration
 * @author mailmindlin
 */
public interface MethodDeclarationTree extends DecoratableTree, PropertyTree, FunctionTree, ClassElementTree, ObjectLiteralElement {
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.METHOD_DECLARATION;
	}
}
