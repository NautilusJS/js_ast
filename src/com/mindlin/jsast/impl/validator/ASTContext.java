package com.mindlin.jsast.impl.validator;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class ASTContext {
	public TypeTree resolveType(IdentifierTypeTree identifier) {
		return null;
	}
	
	public TypeTree getVariableDeclaredType(IdentifierTree identifier) {
		return null;
	}
	
	public TypeTree getVariableImplicitType(IdentifierTree identifier) {
		return null;
	}
}
