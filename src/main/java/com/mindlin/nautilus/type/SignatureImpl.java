package com.mindlin.jsast.type;

import java.util.List;
import java.util.Objects;

public class SignatureImpl implements Signature {
	protected final List<TypeParameter> typeParameters;
	protected final List<ParameterInfo> parameters;
	protected final Type returnType;
	
	public SignatureImpl(List<TypeParameter> typeParams, List<ParameterInfo> params, Type returnType) {
		this.typeParameters = typeParams;
		this.parameters = params;
		this.returnType = returnType;
	}
	
	@Override
	public List<TypeParameter> getTypeParameters() {
		return this.typeParameters;
	}
	
	@Override
	public Object getDeclaration() {
		return null;
	}
	
	@Override
	public List<ParameterInfo> getParameters() {
		return this.parameters;
	}
	
	@Override
	public Type getReturnType() {
		return this.returnType;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getTypeParameters(), this.getDeclaration(), this.getParameters(), this.getReturnType());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Signature))
			return false;
		
		Signature other = (Signature) o;
		
		return Objects.equals(this.getTypeParameters(), other.getTypeParameters())
				&& Objects.equals(this.getDeclaration(), other.getDeclaration())
				&& Objects.equals(this.getParameters(), other.getParameters())
				&& Objects.equals(this.getReturnType(), other.getReturnType());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append('{');
		if (!this.getTypeParameters().isEmpty()) {
			sb.append('<');
			for (TypeParameter generic : this.getTypeParameters())
				sb.append(generic);
			sb.append('>');
		}
		
		sb.append('(');
		boolean firstParam = true;
		for (ParameterInfo param : this.parameters) {
			if (!firstParam)
				sb.append(", ");
			firstParam = false;
			if (param.isRest())
				sb.append("...");
			sb.append(param.getIdentifier());
			if (param.isOptional())
				sb.append('?');
			if (param.getDeclaredType() != null)
				sb.append(": ").append(param.declaredType);
		}
		sb.append(')');
		
		sb.append(" => ");
		sb.append(this.returnType);
		
		sb.append('}');
		return sb.toString();
	}
}
