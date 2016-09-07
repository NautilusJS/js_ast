package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.TypeTree;

public interface IndexTypeTree extends TypeTree {
	TypeTree getIndexType();
	TypeTree getType();
}
