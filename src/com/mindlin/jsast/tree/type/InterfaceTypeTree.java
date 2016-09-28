package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TypeTree;

public interface InterfaceTypeTree extends TypeTree {
	
	List<InterfacePropertyTree> getProperties();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_TYPE;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitInterfaceType(this, data);
	}
}
