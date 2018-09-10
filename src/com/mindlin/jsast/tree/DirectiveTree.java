package com.mindlin.jsast.tree;

public interface DirectiveTree extends StatementTree {
	
	String getDirective();
	
	@Override
	default Kind getKind() {
		return Tree.Kind.DIRECTIVE;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitDirective(this, data);
	}
}
