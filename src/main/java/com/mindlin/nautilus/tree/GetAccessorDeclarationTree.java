package com.mindlin.nautilus.tree;

public interface GetAccessorDeclarationTree extends MethodDeclarationTree {
	@Override
	default Kind getKind() {
		return Kind.GET_ACCESSOR_DECLARATION;
	}
}
