package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TypeTree;

public interface IdentifierTypeTree extends TypeTree {
	IdentifierTree getIdentifier();
	List<TypeTree> getGenerics();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TYPE_IDENTIFIER;
	}
}
