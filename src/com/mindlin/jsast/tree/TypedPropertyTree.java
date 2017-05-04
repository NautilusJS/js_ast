package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.type.TypeTree;

public interface TypedPropertyTree extends ObjectPropertyTree {
	boolean isReadonly();
	
	TypeTree getType();
}
