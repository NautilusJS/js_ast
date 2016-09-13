package com.mindlin.jsast.impl.parser;

public enum JSKeyword {
	AS(true),
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
	ENUM,
	EXPORT,
	EXTENDS,
	FINALLY,
	FOR,
	FROM(true),
	FUNCTION,
	FUNCTION_GENERATOR(false, "function*"),
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
	YIELD_GENERATOR(false, "yield*");
	public static JSKeyword lookup(String identifier) {
		//Check if identifier can even be an identifier (i.e., is all lower case)
		if (!identifier.equals(identifier.toLowerCase()))
			return null;
		for (JSKeyword keyword : JSKeyword.values())
			if (identifier.equals(keyword.toString()))
				return keyword;
		return null;
	}
	private final boolean requiresStrict;
	private final String stringValue;
	JSKeyword() {
		this(false);
	}
	JSKeyword(boolean requiresStrict) {
		this.requiresStrict = requiresStrict;
		this.stringValue = name().toLowerCase();
	}
	JSKeyword(boolean requiresStrict, String stringValue) {
		this.requiresStrict = requiresStrict;
		this.stringValue = stringValue;
	}
	public boolean isStrictOnly() {
		return this.requiresStrict;
	}
	@Override
	public String toString() {
		return stringValue;
	}
}
