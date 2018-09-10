package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Modifiers;

public interface MappedTypeTree extends TypeTree {
	Modifiers getModifiers();
	
	TypeParameterDeclarationTree getParameter();
}
