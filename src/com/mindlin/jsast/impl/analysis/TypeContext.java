package com.mindlin.jsast.impl.analysis;

import java.util.Collections;
import java.util.List;

import com.mindlin.jsast.type.Type;

/**
 * A readonly context that resolves types
 * 
 * @author mailmindlin
 *
 */
public interface TypeContext {
	default Type getType(String name) {
		return getType(name, Collections.emptyList());
	}
	
	Type getType(String name, List<Type> generics);
	
	Type resolveAliases(Type type);
	
	//Type variables
	Type thisType();
	
	Type superType();
	
	Type argumentsType();
	
	default Type intern(Type t) {
		return t;
	}
}
