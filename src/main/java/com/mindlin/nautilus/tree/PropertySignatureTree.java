package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeElementTree;
import com.mindlin.jsast.tree.type.TypeTree;

public interface PropertySignatureTree extends PropertyTree, TypeElementTree {
	TypeTree getType();
	
	@Override
	default Kind getKind() {
		return Kind.PROPERTY_SIGNATURE;
	}
	
	@Override
	default <R, D> R accept(TypeElementVisitor<R, D> visitor, D data) {
		return visitor.visitPropertySignature(this, data);
	}
}