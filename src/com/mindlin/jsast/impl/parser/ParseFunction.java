package com.mindlin.jsast.impl.parser;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;

@FunctionalInterface
public interface ParseFunction<R> {
	R apply(JSLexer src, Context context);
}
