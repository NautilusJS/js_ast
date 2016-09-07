package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.TypeTree;

public interface IdentifierTypeTree extends TypeTree {
	IdentifierTree getIdentifier();
	List<TypeTree> getGenerics();
}
