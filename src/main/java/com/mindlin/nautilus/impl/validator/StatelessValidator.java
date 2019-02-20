package com.mindlin.jsast.impl.validator;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.util.TreePredicate;

public interface StatelessValidator<T extends Tree> extends TreePredicate {
	void check(T node, ErrorReporter reporter);
}
