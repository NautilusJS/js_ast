package com.mindlin.jsast.type;

import java.util.Collection;

public interface ObjectType extends Type, GenericType {
	/**
	 * Get the 'this' type parameter
	 * @return
	 */
	TypeParameter thisType();
	
	
	Collection<TypeMember> declaredProperties();
	
	Collection<Signature> declaredCallSignatures();
	
	Collection<Signature> declaredConstructSignatures();
	
	Collection<IndexInfo> declaredIndexInfo();
	
	
	@Override
	default Type.Kind getKind() {
		return Type.Kind.OBJECT;
	}
}
