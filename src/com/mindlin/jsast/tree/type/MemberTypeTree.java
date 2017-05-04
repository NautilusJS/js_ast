package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

public interface MemberTypeTree extends TypeTree {
	TypeTree getName();
	
	TypeTree getBaseType();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.MEMBER_TYPE;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitMemberType(this, data);
	}
}
