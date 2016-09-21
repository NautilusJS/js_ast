package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.StatementTree;

public class ForLoopTest {
	@Test
	public void testForLoops() {
		StatementTree loop = parseStatement("for(var i in [1,2,3]);");
		//System.out.println(loop);
	}
}
