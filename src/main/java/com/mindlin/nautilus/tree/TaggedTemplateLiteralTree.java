package com.mindlin.jsast.tree;

public interface TaggedTemplateLiteralTree extends ExpressionTree {
	ExpressionTree getTag();
	
	TemplateLiteralTree getQuasi();
	
	@Override
	default Kind getKind() {
		return Kind.TAGGED_TEMPLATE;
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitTaggedTemplate(this, data);
	}
}
