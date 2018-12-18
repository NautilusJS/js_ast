package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.SignatureDeclarationTree;
import com.mindlin.jsast.tree.Tree;

public interface ConstructorTypeTree extends TypeTree, SignatureDeclarationTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CONSTRUCTOR_TYPE;
	}
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitConstructorType(this, data);
	}
}
