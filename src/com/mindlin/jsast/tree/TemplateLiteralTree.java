package com.mindlin.jsast.tree;

import java.util.List;

/**
 * Template literal.
 * 
 * @author mailmindlin
 *
 */
public interface TemplateLiteralTree extends LiteralTree<String> {
	
	/**
	 * The actual strings in this template literal
	 * @return list of quasis, in order
	 */
	List<TemplateElementTree> getQuasis();
	
	/**
	 * Expressions interleaved between quasis
	 * @return list of expressions, in order
	 */
	List<ExpressionTree> getExpressions();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TEMPLATE_LITERAL;
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitTemplateLiteral(this, data);
	}
}
