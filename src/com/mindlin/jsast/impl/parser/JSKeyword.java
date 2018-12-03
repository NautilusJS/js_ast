package com.mindlin.jsast.impl.parser;

import java.util.HashMap;
import java.util.Map;

public enum JSKeyword {
	ABSTRACT(true),
	AS(true),
	ASYNC(true),
	AWAIT(true),
	BREAK,
	CASE,
	CLASS,
	CONST,
	CONSTRUCTOR(true),
	CONTINUE,
	DEBUGGER,
	DECLARE(true),
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
	READONLY(true),
	RETURN,
	STATIC(true),
	SUPER,
	SWITCH,
	THIS,
	THROW,
	TRY,
	TYPE(true),
	TYPEOF,
	VAR,
	VOID,
	WHILE,
	WITH,
	YIELD,
	;
	protected static final Map<String, JSKeyword> LUT = new HashMap<>();
	static {
		for (JSKeyword keyword : JSKeyword.values())
			LUT.put(keyword.toString(), keyword);
	}
	
	public static JSKeyword lookup(String identifier) {
		return LUT.get(identifier);
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
