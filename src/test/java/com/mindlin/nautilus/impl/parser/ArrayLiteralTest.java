package com.mindlin.nautilus.impl.parser;

import static com.mindlin.nautilus.impl.parser.JSParserTest.assertLiteral;
import static com.mindlin.nautilus.impl.parser.JSParserTest.parseExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.mindlin.nautilus.tree.ArrayLiteralTree;
import com.mindlin.nautilus.tree.ObjectLiteralTree;

public class ArrayLiteralTest {
	@Test
	public void testEmpty() {
		ArrayLiteralTree arr = parseExpression("[]");
		assertEquals(0, arr.getElements().size());
	}

	@Test
	public void testSingleValue() {
		ArrayLiteralTree arr = parseExpression("[1]");
		assertEquals(1, arr.getElements().size());
		assertLiteral(1, arr.getElements().get(0));
	}

	@Test
	public void testSingleUndefined() {
		ArrayLiteralTree arr = parseExpression("[,]");
		assertEquals(1, arr.getElements().size());
		assertNull(arr.getElements().get(0));
	}

	@Test
	public void testCommaAfterValue() {
		ArrayLiteralTree arr = parseExpression("[1,]");
		assertEquals(1, arr.getElements().size());
		assertLiteral(1, arr.getElements().get(0));
	}

	@Test
	public void testCommaBeforeValue() {
		ArrayLiteralTree arr = parseExpression("[,1]");
		assertEquals(2, arr.getElements().size());
		assertNull(arr.getElements().get(0));
		assertLiteral(1, arr.getElements().get(1));
	}

	@Test
	public void testMultipleValues() {
		ArrayLiteralTree arr = parseExpression("[1,2]");
		assertEquals(2, arr.getElements().size());
		assertLiteral(1, arr.getElements().get(0));
		assertLiteral(2, arr.getElements().get(1));
	}

	@Test
	public void testNestedArrayLiteral() {
		ArrayLiteralTree arr = parseExpression("[[]]");
		assertEquals(1, arr.getElements().size());
		ArrayLiteralTree nested = (ArrayLiteralTree) arr.getElements().get(0);
		assertEquals(0, nested.getElements().size());
	}
	
	@Test
	public void testNestedObjectLiteral() {
		ArrayLiteralTree arr = parseExpression("[{}]");
		assertEquals(1, arr.getElements().size());
		ObjectLiteralTree nested = (ObjectLiteralTree) arr.getElements().get(0);
		assertEquals(0, nested.getProperties().size());
	}
}
