package com.mindlin.nautilus.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.nautilus.tree.RegExpLiteralTree;
import com.mindlin.nautilus.tree.Tree.Kind;

import static com.mindlin.nautilus.impl.parser.JSParserTest.*;


public class RegExpLiteralTest {
	
	@Test
	public void testEmptyRegExp() {
		RegExpLiteralTree regex = parseExpression("/x/", Kind.REGEXP_LITERAL);
		
		assertEquals("x", regex.getBody());
		assertEquals("", regex.getFlags());
	}
}
