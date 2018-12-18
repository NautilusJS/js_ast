package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.NamedDeclarationTree;
import com.mindlin.jsast.tree.UnvisitableTree;

public interface EnumMemberTree extends NamedDeclarationTree, UnvisitableTree {
	
	/**
	 * (Optional) initializer.
	 * @return initializer
	 */
	ExpressionTree getInitializer();
}
