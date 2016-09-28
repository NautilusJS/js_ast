package com.mindlin.jsast.tree;

public interface CommentNode extends Tree {
	String getText();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.COMMENT;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitComment(this, data);
	}
}