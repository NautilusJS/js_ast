package com.mindlin.jsast.tree;

//TODO change value from String[2] to some better type (e.g., RegExpTokenInfo)
public interface RegExpLiteralTree extends LiteralTree<String[]> {
	
	String getBody();
	
	String getFlags();
	
	@Override
	default Kind getKind() {
		return Kind.REGEXP_LITERAL;
	}

	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitRegExpLiteral(this, data);
	}
}
