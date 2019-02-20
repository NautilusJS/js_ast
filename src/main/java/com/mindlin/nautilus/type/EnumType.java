package com.mindlin.nautilus.type;

public class EnumType implements Type {

	@Override
	public Kind getKind() {
		return Kind.ENUM;
	}
	
}
