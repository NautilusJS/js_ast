package com.mindlin.jsast.tree;

public interface MemberSelectTree extends ExpressiveExpressionTree, ExpressionTree {
        IdentifierTree getIdentifier();
}