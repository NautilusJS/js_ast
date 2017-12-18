package com.mindlin.jsast.type;

import java.util.Collection;
import java.util.List;

public class ObjectTypeImpl implements ObjectType {
	List<TypeParameter> generics;
	TypeParameter thisTP;
	Collection<TypeMember> props;
	Collection<Signature> callSignatures;
	Collection<Signature> constructSignatures;
	Collection<IndexInfo> indices;
	
	public ObjectTypeImpl(List<TypeParameter> generics, TypeParameter thisTP, Collection<TypeMember> props, Collection<Signature> callSignatures, Collection<Signature> constructSignatures, Collection<IndexInfo> indices) {
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
	public Collection<TypeMember> declaredProperties() {
		return this.props;
	}

	@Override
	public Collection<Signature> declaredCallSignatures() {
		return this.callSignatures;
	}

	@Override
	public Collection<Signature> declaredConstructSignatures() {
		return this.constructSignatures;
	}

	@Override
	public Collection<IndexInfo> declaredIndexInfo() {
		return this.indices;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		
		{
			sb.append('<');
			boolean first = true;
			for (TypeParameter param : this.getTypeParameters()) {
				if (!first)
					sb.append(", ");
				sb.append(param);
				first = false;
			}
			sb.append('>');
		}
		
		sb.append('{');
		
		boolean first = true;
		for (TypeMember prop : this.declaredProperties()) {
			sb.append("\n\t");
			sb.append(prop);
		}
		
		
		sb.append('}');
		return sb.toString();
	}
}
