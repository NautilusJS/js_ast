package com.mindlin.jsast.type;

import java.util.List;
import java.util.ListIterator;

/**
 * Generic function signature. Note how this doesn't inherit from Type.
 * 
 * @author mailmindlin
 *
 */
public interface Signature {
	Object getDeclaration();//TODO: Needed?
	
	/**
	 * Get list of type parameters. May return null if no type parameters are
	 * present on this signature.
	 * 
	 * @return list of type parameters
	 */
	List<TypeParameter> getTypeParameters();
	
	/**
	 * Get maximum acceptable number of type arguments.
	 * Because {@link #getTypeParameters()} may return {@code null} if no
	 * type parameters are present, using this method is just a bit cleaner than
	 * putting a null check everywhere.
	 * 
	 * @return maximum acceptable number of type arguments
	 */
	default int maxTypeParameterCount() {
		List<TypeParameter> parameters = this.getTypeParameters();
		return parameters == null ? 0 : parameters.size();
	}
	
	/**
	 * Get list of parameters.
	 * @return list of parameters (never null)
	 */
	List<ParameterInfo> getParameters();
	
	/**
	 * Get return type.
	 * TODO: may this method return null?
	 * @return return type
	 */
	Type getReturnType();
	
	/**
	 * Get the minimum required number of arguments (number of parameters excluding optional and rest parameters).
	 * <p>
	 * For example, a signature with parameters {@code (a: P, b?: Q)} would require 1 argument.
	 * </p> 
	 * @return minimum required number of arguments
	 */
	default int minArgumentCount() {
		List<ParameterInfo> parameters = this.getParameters();
		
		int result = parameters.size();
		for (ListIterator<ParameterInfo> i = parameters.listIterator(parameters.size() - 1); i.hasPrevious(); result--) {
			ParameterInfo param = i.previous();
			if (!(param.isOptional() || param.isRest() || param.getInitializer() != null))
				return result;
		}
		
		return 0;
	}
}
