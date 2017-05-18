package com.mindlin.jsast.tree.type;

import java.util.Optional;

import com.mindlin.jsast.tree.IdentifierTree;

public interface GenericRefTypeTree extends TypeTree {
	IdentifierTree getName();
	
	Optional<GenericTypeTree> getReferenced();
}
