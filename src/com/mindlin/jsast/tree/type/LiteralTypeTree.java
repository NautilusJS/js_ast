package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.LiteralTree;

public interface LiteralTypeTree<T> extends TypeTree {
	LiteralTree<T> getValue();
	
	@Override
	default Kind getKind() {
		return Kind.LITERAL_TYPE;
	}

	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitLiteralType(this, data);
	}
}
