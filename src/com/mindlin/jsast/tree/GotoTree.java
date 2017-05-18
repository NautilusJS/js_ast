package com.mindlin.jsast.tree;

public interface GotoTree extends StatementTree {
	IdentifierTree getLabel();
}
