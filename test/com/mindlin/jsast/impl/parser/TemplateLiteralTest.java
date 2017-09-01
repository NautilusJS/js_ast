package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.TemplateElementTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;
import com.mindlin.jsast.tree.Tree.Kind;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

public class TemplateLiteralTest {
	
	@Test
	public void testTemplateString() {
		TemplateLiteralTree template = parseExpression("`Hello`", Kind.TEMPLATE_LITERAL);
		
		List<TemplateElementTree> quasis = template.getQuasis();
		assertEquals(quasis.size(), 1);
		
		TemplateElementTree t0 = quasis.get(0);
		assertEquals("Hello", t0.getCooked());
	}
	
	@Test
	public void testTemplateOnlyExpr() {
		TemplateLiteralTree template = parseExpression("`${1+2}`", Kind.TEMPLATE_LITERAL);

		List<TemplateElementTree> quasis = template.getQuasis();
		assertEquals(quasis.size(), 2);
		assertEquals("", quasis.get(0).getCooked());
		assertEquals("", quasis.get(1).getCooked());
		
		List<ExpressionTree> exprs = template.getExpressions();
		assertEquals(exprs.size(), 1);
		BinaryTree expr = assertKind(exprs.get(0), Kind.ADDITION);
		assertLiteral(1, expr.getLeftOperand());
		assertLiteral(2, expr.getRightOperand());
	}
	
	@Test
	public void testTemplateComplex() {
		TemplateLiteralTree template = parseExpression("`Hello, ${'world!'}`", Kind.TEMPLATE_LITERAL);
		
		//TODO check
	}
}
