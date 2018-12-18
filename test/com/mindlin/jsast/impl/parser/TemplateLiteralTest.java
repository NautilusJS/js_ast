package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryExpressionTree;
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
		assertEquals(1, quasis.size());
		
		TemplateElementTree t0 = quasis.get(0);
		assertEquals("Hello", t0.getCooked());
	}
	
	@Test
	public void testTemplateOnlyExpr() {
		TemplateLiteralTree template = parseExpression("`${1+2}`", Kind.TEMPLATE_LITERAL);

		List<TemplateElementTree> quasis = template.getQuasis();
		assertEquals(2, quasis.size());
		assertEquals("", quasis.get(0).getCooked());
		assertEquals("", quasis.get(1).getCooked());
		
		List<ExpressionTree> exprs = template.getExpressions();
		assertEquals(1, exprs.size());
		BinaryExpressionTree expr = assertKind(Kind.ADDITION, exprs.get(0));
		assertLiteral(1, expr.getLeftOperand());
		assertLiteral(2, expr.getRightOperand());
	}
	
	@Test
	public void testTemplateComplex() {
		TemplateLiteralTree template = parseExpression("`Hello, ${'world!'}`", Kind.TEMPLATE_LITERAL);
		List<TemplateElementTree> quasis = template.getQuasis();
		assertEquals(2, quasis.size());
		assertEquals("Hello, ", quasis.get(0).getCooked());
		assertEquals("", quasis.get(1).getCooked());
		
		List<ExpressionTree> exprs = template.getExpressions();
		assertEquals(1, exprs.size());
		assertLiteral("world!", exprs.get(0));
	}
}
