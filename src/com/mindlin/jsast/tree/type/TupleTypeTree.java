package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.TypeTree;

public interface TupleTypeTree extends TypeTree {
	List<TypeTree> getSlotTypes();
}
