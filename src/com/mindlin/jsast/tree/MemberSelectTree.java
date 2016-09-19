package com.mindlin.jsast.tree;

public interface MemberSelectTree extends ExpressiveExpressionTree, ExpressionTree {
	IdentifierTree getIdentifier();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.MEMBER_SELECT;
	}
}