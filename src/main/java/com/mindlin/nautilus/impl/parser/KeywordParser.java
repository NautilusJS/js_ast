package com.mindlin.nautilus.impl.parser;

import com.mindlin.nautilus.impl.lexer.JSLexer;
import com.mindlin.nautilus.impl.lexer.Token;
import com.mindlin.nautilus.tree.Tree;

@FunctionalInterface
public interface KeywordParser<T extends Tree> {
	T parse(Token token, JSLexer src, boolean isStrict);
}
