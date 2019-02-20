package com.mindlin.jsast.tree.comment;

import com.mindlin.jsast.tree.IdentifierTree;

public interface JSDocTag {
	IdentifierTree getName();
	String getComment();
}
