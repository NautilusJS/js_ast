package com.mindlin.nautilus.impl.parser;

import com.mindlin.nautilus.impl.lexer.JSLexer;
import com.mindlin.nautilus.impl.parser.JSParser.Context;

@FunctionalInterface
public interface ParseFunction<R> {
	R apply(JSLexer src, Context context);
}
