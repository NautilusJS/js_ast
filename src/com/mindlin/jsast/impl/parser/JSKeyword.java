package com.mindlin.jsast.impl.parser;

public enum JSKeyword {
	AWAIT,
	BREAK,
	CASE,
	CATCH,
	CLASS,
	CONST,
	CONTINUE,
	DEBUGGER,
	DEFAULT,
	DELETE,
	DO,
	ELSE,
	ENUM,//Doesn't do anything, but can't be used as an identifier
	EXPORT,
	EXTENDS,
	FINALLY,
	FOR,
	FUNCTION,
	FUNCTION_GENERATOR,
	IF,
	IMPLEMENTS(true),
	IMPORT,
	IN,
	INSTANCEOF,
	INTERFACE(true),
	LET(true),
	NEW,
	OF(true),
	PACKAGE(true),
	PRIVATE(true),
	PROTECTED(true),
	PUBLIC(true),
	RETURN,
	STATIC(true),
	SUPER,
	SWITCH,
	THIS,
	THROW,
	TRY,
	TYPEOF,
	VAR,
	VOID,
	WHILE,
	WITH,
	YIELD,
	YIELD_GENERATOR;
	public static JSKeyword lookup(String identifier) {
		//TODO optimize
		for (JSKeyword keyword : JSKeyword.values())
			if (identifier.equalsIgnoreCase(keyword.name()))
				return keyword;
		if (identifier.equalsIgnoreCase("function*"))
			return FUNCTION_GENERATOR;
		if (identifier.equalsIgnoreCase("yield*"))
			return YIELD_GENERATOR;
		return null;
	}
	private final boolean requiresStrict;
	JSKeyword() {
		this(false);
	}
	JSKeyword(boolean requiresStrict) {
		this.requiresStrict = requiresStrict;
	}
	public boolean isStrictOnly() {
		return this.requiresStrict;
	}
}
