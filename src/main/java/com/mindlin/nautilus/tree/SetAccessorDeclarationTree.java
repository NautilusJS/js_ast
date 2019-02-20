package com.mindlin.jsast.tree;

public interface SetAccessorDeclarationTree extends MethodDeclarationTree {
	@Override
	default Kind getKind() {
		return Kind.SET_ACCESSOR_DECLARATION;
	}
}
