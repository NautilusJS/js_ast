package com.mindlin.jsast.type;

import java.util.List;

public interface GenericType extends Type {
	List<TypeParameter> getTypeParameters();
	
	default int maxTypeParameterCount() {
		List<TypeParameter> parameters = this.getTypeParameters();
		return parameters == null ? 0 : parameters.size();
	}
}
