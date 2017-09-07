package com.mindlin.jsast.tree;

public interface MethodDefinitionTree extends ClassPropertyTree<FunctionExpressionTree>, ObjectLiteralPropertyTree {
	
	boolean isAbstract();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.METHOD_DEFINITION;
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		return ClassPropertyTree.super.equivalentTo(other)
				&& (other instanceof MethodDefinitionTree)
				&& (this.isAbstract() == ((MethodDefinitionTree)other).isAbstract());
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
