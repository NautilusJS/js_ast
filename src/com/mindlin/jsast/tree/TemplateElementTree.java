package com.mindlin.jsast.tree;

public interface TemplateLiteralTree extends Tree {
	
	String getRaw();
	
	boolean isTail();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TEMPLATE_ELEMENT;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
