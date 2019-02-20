package com.mindlin.jsast.type;

public enum IntrinsicType implements Type {
	ANY,
	NUMBER,
	BOOLEAN,
	STRING,
	SYMBOL,
	VOID,
	
	//Not real intrinsics
	NULL,
	UNDEFINED,
	NEVER,
	;
	
	@Override
	public Kind getKind() {
		return Type.Kind.INTRINSIC;
	}
}
