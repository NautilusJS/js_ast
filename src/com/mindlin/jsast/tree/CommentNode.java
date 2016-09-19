package com.mindlin.jsast.tree;

public interface CommentNode extends Tree {
	String getText();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.COMMENT;
	}
}