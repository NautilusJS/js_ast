package com.mindlin.jsast.tree;

public interface MethodDefinitionTree extends ClassPropertyTree<FunctionExpressionTree> {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.METHOD_DEFINITION;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitMethodDefinition(this, data);
	}
}
