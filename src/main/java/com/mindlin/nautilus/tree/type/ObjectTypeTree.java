package com.mindlin.nautilus.tree.type;

import java.util.List;

import com.mindlin.nautilus.tree.Tree;

public interface ObjectTypeTree extends TypeTree {
	
	List<TypeElementTree> getDeclaredMembers();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.OBJECT_TYPE;
	}

	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitInterfaceType(this, data);
	}
}
