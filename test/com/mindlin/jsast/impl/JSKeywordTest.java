package com.mindlin.jsast.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.parser.JSKeyword;

public class JSKeywordTest {
	
	@Test
	public void testLookup() {
		assertEquals(JSKeyword.IF, JSKeyword.lookup("if"));
		
		assertEquals(JSKeyword.FUNCTION, JSKeyword.lookup("function"));
		
		assertEquals(JSKeyword.YIELD, JSKeyword.lookup("yield"));
	}
	
	@Test
	public void testLookupMixedCase() {
		assertNull(JSKeyword.lookup("IF"));
		assertNull(JSKeyword.lookup("If"));
		assertNull(JSKeyword.lookup("If"));
	}
}
