package com.mindlin.jsast.impl.analysis;

import java.util.List;

import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public interface TypeContext {
	TypeTree resolve(IdentifierTypeTree type);
	TypeTree resolve(String name);
	List<TypeTree> resolveFunction(String name);
}
