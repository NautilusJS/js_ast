package com.mindlin.jsast.tree;

public interface ExportTree extends StatementTree {
	ExpressionTree getExpression();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.EXPORT;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitExport(this, data);
	}
}
