package com.mindlin.jsast.tree;

public interface TypedPropertyTree extends ObjectPropertyTree {
	boolean isReadonly();
	
	TypeTree getType();
}
