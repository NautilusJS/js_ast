package com.mindlin.nautilus.tree.type;

import com.mindlin.nautilus.tree.SignatureDeclarationTree;
import com.mindlin.nautilus.tree.Tree;

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
