package com.mindlin.nautilus.impl.validator;

import com.mindlin.nautilus.tree.Tree;
import com.mindlin.nautilus.tree.util.TreePredicate;

public interface StatelessValidator<T extends Tree> extends TreePredicate {
	void check(T node, ErrorReporter reporter);
}
