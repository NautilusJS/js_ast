package com.mindlin.jsast.tree;

public interface ExportTree extends ExpressiveStatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.EXPORT;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitExport(this, data);
	}
}
