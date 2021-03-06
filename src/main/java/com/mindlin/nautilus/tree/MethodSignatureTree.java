package com.mindlin.nautilus.tree;

import com.mindlin.nautilus.tree.type.TypeElementTree;

public interface MethodSignatureTree extends DecoratableTree, SignatureDeclarationTree, PropertyTree, TypeElementTree {
	@Override
	default Kind getKind() {
		return Kind.METHOD_SIGNATURE;
	}
	
	@Override
	default <R, D> R accept(TypeElementVisitor<R, D> visitor, D data) {
		return visitor.visitMethodSignature(this, data);
	}
}
