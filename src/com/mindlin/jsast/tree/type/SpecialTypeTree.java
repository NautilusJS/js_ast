package com.mindlin.jsast.tree.type;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;

/**
 * Type tree representing built-in primitive types
 * @author mailmindlin
 */
public interface SpecialTypeTree extends TypeTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.SPECIAL_TYPE;
	}
	
	SpecialType getType();
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitSpecialType(this, data);
	}
	
	public static enum SpecialType {
		ANY,
		NEVER,
		VOID,
		NULL,
		UNDEFINED,
		STRING,
		BOOLEAN,
		NUMBER,
		;
	}
}
