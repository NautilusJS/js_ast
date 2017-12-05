package com.mindlin.jsast.type;

import java.util.List;
import java.util.Set;

public class ObjectTypeImpl implements ObjectType {
	List<TypeParameter> generics;
	TypeParameter thisTP;
	Set<TypeMember> props;
	Set<Signature> callSignatures;
	Set<Signature> constructSignatures;
	Set<IndexInfo> indices;
	
	public ObjectTypeImpl(List<TypeParameter> generics, TypeParameter thisTP, Set<TypeMember> props, Set<Signature> callSignatures, Set<Signature> constructSignatures, Set<IndexInfo> indices) {
		this.generics = generics;
		this.thisTP = thisTP;
		this.props = props;
		this.callSignatures = callSignatures;
		this.constructSignatures = constructSignatures;
		this.indices = indices;
	}
	
	@Override
	public List<TypeParameter> getTypeParameters() {
		return this.generics;
	}

	@Override
	public TypeParameter thisType() {
		return this.thisTP;
	}

	@Override
	public Set<TypeMember> declaredProperties() {
		return this.props;
	}

	@Override
	public Set<Signature> declaredCallSignatures() {
		return this.callSignatures;
	}

	@Override
	public Set<Signature> declaredConstructSignatures() {
		return this.constructSignatures;
	}

	@Override
	public Set<IndexInfo> declaredIndexInfo() {
		return this.indices;
	}
	
}
