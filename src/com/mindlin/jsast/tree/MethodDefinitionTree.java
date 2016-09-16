package com.mindlin.jsast.tree;

public interface MethodDefinitionTree extends ObjectLiteralPropertyTree {
	@Override
	FunctionExpressionTree getValue();

	MethodDefinitionType getPropertyType();

	boolean isStatic();
	
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
