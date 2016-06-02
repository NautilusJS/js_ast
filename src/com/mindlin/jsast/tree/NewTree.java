package com.mindlin.jsast.tree;

public interface NewTree extends ExpressionTree {
        ExpressionTree getConstructorExpression();
}