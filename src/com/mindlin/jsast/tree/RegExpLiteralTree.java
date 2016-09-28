package com.mindlin.jsast.tree;

public interface RegExpLiteralTree extends LiteralTree<String[]> {
	
	@Override
	default Kind getKind() {
		return Kind.REGEXP_LITERAL;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitRegExpLiteral(this, data);
	}
}
