package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.Tree;

public class ForLoopTest {
	@Test
	public void testForInLoop() {
		StatementTree loopStmt = parseStatement("for(var i in [1,2,3]);");
		assertEquals(Tree.Kind.FOR_IN_LOOP, loopStmt.getKind());
	}
	
	@Test
	public void testForLoop() {
		StatementTree loopStmt = parseStatement("for(var i = 0; i < 10; i++);");
		assertEquals(Tree.Kind.FOR_LOOP, loopStmt.getKind());
		ForLoopTree loop = (ForLoopTree) loopStmt;
	}
	
	@Test
	public void testForOfLoop() {
		StatementTree loopStmt = parseStatement("for(var i of [1, 2, 3]);");
		assertEquals(Tree.Kind.FOR_OF_LOOP, loopStmt.getKind());
	}
}
