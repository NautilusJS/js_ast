package com.mindlin.jsast.tree;

public interface MethodDefinitionTree extends ObjectLiteralPropertyTree {
	@Override
	FunctionExpressionTree getValue();

	MethodDefinitionType getPropertyType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.METHOD_DEFINITION;
	}

	public static enum MethodDefinitionType {
		GETTER,
		SETTER,
		METHOD,
		CONSTRUCTOR;
	}
}
