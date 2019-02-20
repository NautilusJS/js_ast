package com.mindlin.nautilus.transform;

import com.mindlin.nautilus.tree.type.TypeParameterDeclarationTree;
import com.mindlin.nautilus.tree.type.TypeTree;
import com.mindlin.nautilus.type.Type;

public interface TypeContext {
	TypeTree getTypeForName(String name);
	void defineType(String name, Type type);
	void undefineType(String name);
	void defineGenericParameter(TypeParameterDeclarationTree parameter);
}
