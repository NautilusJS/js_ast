package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ForLoopTree;

public class ForLoopTest {
	@Test
	public void testForLoops() {
		ForLoopTree loop = (ForLoopTree)parseStatement("for(var i in [1,2,3]);");
		System.out.println(loop);
	}
}
