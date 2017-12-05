package com.mindlin.jsast.type;

import java.util.Set;

public interface ObjectType extends Type, GenericType {
	/**
	 * Get the 'this' type parameter
	 * @return
	 */
	TypeParameter thisType();
	
	Set<TypeMember> declaredProperties();
	
	Set<Signature> declaredCallSignatures();
	
	Set<Signature> declaredConstructSignatures();
	
	Set<IndexInfo> declaredIndexInfo();
	
	@Override
	default Type.Kind getKind() {
		return Type.Kind.OBJECT;
	}
}
