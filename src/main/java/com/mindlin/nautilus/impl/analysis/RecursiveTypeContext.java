package com.mindlin.jsast.impl.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindlin.jsast.type.Type;

/**
 * Lightweight (memory) inheriting TypeContext.
 * <p>
 * NOT thread safe
 * </p>
 * @author mailmindlin
 *
 */
public class RecursiveTypeContext implements TypeContext {
	protected static final Object THIS_KEY = new Object(), SUPER_KEY = new Object(), ARGUMENTS_KEY = new Object();
	TypeContext parent;
	boolean modified = false;
	Map<String, Type> localTypes;
	
	public static RecursiveTypeContext inherit(TypeContext parent) {
		return new RecursiveTypeContext(parent);
	}
	
	public static RecursiveTypeContext inheritStatic(TypeContext parent) {
		throw new UnsupportedOperationException();
	}
	
	protected RecursiveTypeContext(TypeContext parent) {
		this.parent = parent;
	}
	
	public void put(String name, Type bound) {
		this.modified = true;
		
		if (this.localTypes == null)
			this.localTypes = new HashMap<>();
		
		this.localTypes.put(name, bound);
	}
	
	public boolean isModified() {
		return this.modified;
	}

	@Override
	public Type getType(String name, List<Type> generics) {
		Type localBound = this.localTypes.get(name);
		if (localBound != null) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Needs binding");
		}
		
		if (this.parent != null)
			return this.parent.getType(name, generics);
		
		throw new IllegalArgumentException("Cannot resolve type: " + name);
	}

	@Override
	public Type resolveAliases(Type type) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Type thisType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Type superType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Type argumentsType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
