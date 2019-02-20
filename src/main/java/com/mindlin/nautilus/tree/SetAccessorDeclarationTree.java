package com.mindlin.nautilus.tree;

public interface SetAccessorDeclarationTree extends MethodDeclarationTree {
	@Override
	default Kind getKind() {
		return Kind.SET_ACCESSOR_DECLARATION;
	}
}
