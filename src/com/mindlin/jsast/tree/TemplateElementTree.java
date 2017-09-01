package com.mindlin.jsast.tree;

public interface TemplateElementTree extends Tree {
	
	String getRaw();
	
	String getCooked();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TEMPLATE_ELEMENT;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}
