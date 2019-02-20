package com.mindlin.jsast.type;

public class EnumType implements Type {

	@Override
	public Kind getKind() {
		return Kind.ENUM;
	}
	
}
