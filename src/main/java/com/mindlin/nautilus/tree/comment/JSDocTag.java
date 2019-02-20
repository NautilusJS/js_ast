package com.mindlin.nautilus.tree.comment;

import com.mindlin.nautilus.tree.IdentifierTree;

public interface JSDocTag {
	IdentifierTree getName();
	String getComment();
}
