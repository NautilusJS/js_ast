package com.mindlin.jsast.tree.type;

public interface ConditionalTypeTree extends TypeTree {
	TypeTree getCheckType();
	TypeTree getLimitType();
	TypeTree getConecquent();
	TypeTree getAlternate();
	
	@Override
	default Kind getKind() {
		return Kind.CONDITIONAL_TYPE;
	}
	
	@Override
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitConditionalType(this, data);
	}
}
