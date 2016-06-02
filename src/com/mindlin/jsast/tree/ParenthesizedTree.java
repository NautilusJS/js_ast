package com.mindlin.jsast.tree;

public interface ParenthesizedTree extends ExpressiveExpressionTree, ExpressionTree {
        default Tree.Kind getKind() {
                return Tree.Kind.PARENTHESIZED;
        }
}