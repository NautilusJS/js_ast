package com.mindlin.nautilus.tree.comment;

import com.mindlin.nautilus.tree.Tree;
import com.mindlin.nautilus.tree.TreeVisitor;

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