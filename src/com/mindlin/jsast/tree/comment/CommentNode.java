package com.mindlin.jsast.tree.comment;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

public interface CommentNode extends Tree {
	String getComment();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.COMMENT;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitComment(this, data);
	}
}