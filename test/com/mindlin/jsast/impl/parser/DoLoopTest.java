package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.WhileLoopTree;

public class DoLoopTest {
	
	@Test
	public void testWhileLoopEmpty() {
		WhileLoopTree loop = parseStatement("while(condition);", Kind.WHILE_LOOP);
		assertIdentifier("condition", loop.getCondition());
	}
	
	@Test
	public void testWhileLoopBlock() {
		WhileLoopTree loop = parseStatement("while(condition){}", Kind.WHILE_LOOP);
		assertIdentifier("condition", loop.getCondition());
	}
	
	@Test
	public void testDoWhileBlock() {
		DoWhileLoopTree loop = parseStatement("do{}while(condition);", Kind.DO_WHILE_LOOP);
		assertIdentifier("condition", loop.getCondition());
	}
}
