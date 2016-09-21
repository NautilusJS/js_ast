package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class OperatorTest {
	
	@Test
	public void testConditional() {
		parseExpression("a?b:c");
	}
	
}
