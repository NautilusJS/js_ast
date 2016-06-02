package com.mindlin.jsast.tree;

import java.util.List;

public interface TryTree extends StatementTree {
	BlockTree getBlock();
	List<? extends CatchTree> getCatches();
	BlockTree getFinallyBlock();
}
