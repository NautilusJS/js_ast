package com.mindlin.jsast.tree;

public interface TemplateLiteralTree extends LiteralTree<String>, ObjectPropertyKeyTree {
	
	List<TemplateElementTree> getQuasis();
	
	List<ExpressionTree> getExpressions();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TEMPLATE_LITERAL;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitTemplateLiteral(this, data);
	}
}
