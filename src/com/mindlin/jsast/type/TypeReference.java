package com.mindlin.jsast.type;

import java.util.List;
import java.util.Objects;

public class TypeReference implements Type {
	public static IncompleteTypeReference unresolved() {
		return new IncompleteTypeReference(null, null);
	}
	
	GenericType target;
	List<Type> arguments;
	
	public TypeReference(GenericType target, List<Type> arguments) {
		this.target = target;
		this.arguments = arguments;
	}
	
	public GenericType getTarget() {
		return this.target;
	}
	
	public List<Type> getTypeArguments() {
		return this.arguments;
	}
	
	@Override
	public Kind getKind() {
		return Kind.REFERENCE;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(target, arguments);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null || !(other instanceof TypeReference))
			return false;
		
		return Objects.equals(this.getTarget(), ((TypeReference) other).getTarget())
				&& Objects.equals(this.getTypeArguments(), ((TypeReference) other).getTypeArguments());
	}
	
	public static class IncompleteTypeReference extends TypeReference {
		
		IncompleteTypeReference(GenericType target, List<Type> arguments) {
			super(target, arguments);
		}
		
		public void resolve(GenericType target, List<Type> arguments) {
			this.target = target;
			this.arguments = arguments;
		}
		
	}
}
