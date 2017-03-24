package com.mindlin.jsast.json.api;

public interface JSONValue {
	
	public static enum Type {
		NULL,
		BOOLEAN,
		NUMBER,
		STRING,
		ARRAY,
		OBJECT;
	}
	
	Type getType();
}
