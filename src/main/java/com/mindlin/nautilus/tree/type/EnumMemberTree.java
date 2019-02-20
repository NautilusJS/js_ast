package com.mindlin.nautilus.tree.type;

import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.NamedDeclarationTree;
import com.mindlin.nautilus.tree.UnvisitableTree;

public interface EnumMemberTree extends NamedDeclarationTree, UnvisitableTree {
	
	/**
	 * (Optional) initializer.
	 * @return initializer
	 */
	ExpressionTree getInitializer();
}
