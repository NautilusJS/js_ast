package com.mindlin.jsast.tree;

public interface MethodDefinitionTree extends ObjectLiteralPropertyTree {
	@Override
	FunctionExpressionTree getValue();

	MethodDefinitionType getPropertyType();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.METHOD_DEFINITION;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitMethodDefinition(this, data);
	}

	public static enum MethodDefinitionType {
		GETTER,
		SETTER,
		METHOD,
		CONSTRUCTOR;
	}
}
