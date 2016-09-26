package com.mindlin.jsast.tree;

public interface ObjectLiteralPropertyTree extends ObjectPropertyTree {
	ExpressionTree getValue();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_LITERAL_PROPERTY;
	}
}
