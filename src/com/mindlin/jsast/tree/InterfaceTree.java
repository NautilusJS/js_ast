package com.mindlin.jsast.tree;

import java.util.List;

public interface InterfaceTree extends StatementTree {
	List<? extends InterfacePropertyTree> getProperties();

	default Tree.Kind getKind() {
		return Tree.Kind.INTERFACE_DECLARATION;
	}

	public interface InterfacePropertyTree {
		boolean isOptional();

		boolean isFunction();
	}
}
