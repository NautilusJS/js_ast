package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.TemplateElementTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;
import com.mindlin.jsast.tree.Tree.Kind;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

public class TemplateLiteralTest {
	
	@Test
	public void testTemplateString() {
		TemplateLiteralTree template = parseExpression("`Hello`", Kind.TEMPLATE_LITERAL);
		System.out.println(template);
		List<TemplateElementTree> quasis = template.getQuasis();
		assertEquals(quasis.size(), 1);
	}
	
	@Test
	public void testTemplateOnlyExpr() {
		TemplateLiteralTree template = parseExpression("`${1+1}`", Kind.TEMPLATE_LITERAL);
		System.out.println(template);
	}
	
	@Test
	public void testTemplateComplex() {
		TemplateLiteralTree template = parseExpression("`Hello, ${'world!'}`", Kind.TEMPLATE_LITERAL);
		System.out.println(template);
	}
}
