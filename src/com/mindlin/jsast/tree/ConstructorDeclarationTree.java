package com.mindlin.jsast.tree;

public interface ConstructorDeclarationTree extends DecoratableTree, PropertyTree, FunctionTree, ClassElementTree {
	@Override
	default Kind getKind() {
		return Kind.CONSTRUCTOR_DECLARATION;
	}
}
