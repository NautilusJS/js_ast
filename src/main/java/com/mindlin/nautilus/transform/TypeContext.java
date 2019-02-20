package com.mindlin.jsast.transform;

import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.type.Type;

public interface TypeContext {
	TypeTree getTypeForName(String name);
	void defineType(String name, Type type);
	void undefineType(String name);
	void defineGenericParameter(TypeParameterDeclarationTree parameter);
}
