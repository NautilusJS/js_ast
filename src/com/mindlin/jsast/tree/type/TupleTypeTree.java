package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

public interface TupleTypeTree extends TypeTree {
	List<TypeTree> getSlotTypes();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TUPLE_TYPE;
	}
}
