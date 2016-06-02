package com.mindlin.jsast.tree;

import java.util.List;

public interface ImportStatementNode extends ExpressionTree {
	List<? extends ExpressionTree> getTargets();
	String getFrom();
}