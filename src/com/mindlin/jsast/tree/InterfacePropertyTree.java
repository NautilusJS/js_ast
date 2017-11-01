package com.mindlin.jsast.tree;

public interface InterfacePropertyTree extends TypedPropertyTree {
	
	boolean isOptional();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_PROPERTY;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}