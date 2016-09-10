package com.mindlin.jsast.tree;

import java.util.List;

public interface ObjectLiteralTree extends ExpressionTree {
	List<? extends PropertyTree> getProperties();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_LITERAL;
	}
}