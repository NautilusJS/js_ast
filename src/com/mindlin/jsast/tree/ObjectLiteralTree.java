package com.mindlin.jsast.tree;

import java.util.List;

public interface ObjectLiteralTree extends ExpressionTree {
        List<? extends PropertyTree> getProperties();
}