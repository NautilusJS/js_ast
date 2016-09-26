package com.mindlin.jsast.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.mindlin.jsast.impl.parser.JSParserTest;

@RunWith(Suite.class)
@SuiteClasses({ CharacterStreamTest.class, JSKeywordTest.class, JSLexerTest.class, JSParserTest.class })
public class AllTests {

}
