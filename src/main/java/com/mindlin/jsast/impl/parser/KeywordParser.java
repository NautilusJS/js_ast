package com.mindlin.jsast.impl.parser;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.Tree;

@FunctionalInterface
public interface KeywordParser<T extends Tree> {
	T parse(Token token, JSLexer src, boolean isStrict);
}
