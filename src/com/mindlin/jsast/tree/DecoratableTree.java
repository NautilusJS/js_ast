package com.mindlin.jsast.tree;

import java.util.List;

public interface DecoratableTree extends Tree {
	List<DecoratorTree> getDecorators();
}
