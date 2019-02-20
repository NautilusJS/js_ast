package com.mindlin.nautilus.tree;

import java.util.List;

/**
 * Declarations that may be decorated
 * @author mailmindlin
 */
public interface DecoratableTree extends Tree {
	List<DecoratorTree> getDecorators();
}
