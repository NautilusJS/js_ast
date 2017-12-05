package com.mindlin.jsast.impl.runtime.objects;

import java.util.HashMap;
import java.util.Map;

import com.mindlin.jsast.exception.JSTypeError;
import com.mindlin.jsast.impl.runtime.JSRuntimeUtils;

public abstract class AbstractJSObject implements JSObject {
	/**
	 * If this class is currently frozen
	 */
	protected boolean frozen;
	/**
	 * If this class is currently sealed
	 */
	protected boolean sealed;
	
	public final Map<Object, PropertyDescriptor> members;
	
	public AbstractJSObject() {
		this(new HashMap<>());
	}
	
	public AbstractJSObject(Map<Object, PropertyDescriptor> members) {
		this.members = members;
	}
	
	@Override
	public Object call(Object thiz, Object... args) {
		PropertyDescriptor fn = members.get(Symbol.callSite);
		if (fn == null)
			throw new JSTypeError(thiz + " is not a function");
		return JSRuntimeUtils.invoke(fn.getter().get(), thiz, args);
	}

	@Override
	public Object callNew(Object... args) {
		PropertyDescriptor fn = members.get(Symbol.constructor);
		if (fn == null)
			throw new JSTypeError(this + " cannot be instantiated");
		return JSRuntimeUtils.invokeNew(fn.getter().get(), args);
	}

	@Override
	public Object getMember(String key) {
		PropertyDescriptor descriptor = members.get(key);
		if (descriptor == null)
			return null;
		return descriptor.getter().get();
	}

	@Override
	public Object getMember(Symbol key) {
		PropertyDescriptor descriptor = members.get(key);
		if (descriptor == null)
			return null;
		return descriptor.getter().get();
	}

	@Override
	public void setMember(String key, Object value) {
		if (isFrozen())
			return;
		members.computeIfAbsent(key, k->(new ValuePropertyDescriptor())).setter().accept(value);
	}

	@Override
	public void setMember(Symbol key, Object value) {
		if (isFrozen())
			return;
		members.computeIfAbsent(key, k->(new ValuePropertyDescriptor())).setter().accept(value);
	}

	@Override
	public boolean removeMember(String key) {
		if (isFrozen())
			return false;
		PropertyDescriptor descriptor = members.get(key);
		if (!descriptor.isConfigurable())
			return false;
		return members.remove(key, descriptor);
	}

	@Override
	public boolean removeMember(Symbol key) {
		if (isFrozen())
			return false;
		PropertyDescriptor descriptor = members.get(key);
		if (!descriptor.isConfigurable())
			return false;
		return members.remove(key, descriptor);
	}

	@Override
	public boolean hasMember(String key) {
		return members.containsKey(key);
	}

	@Override
	public boolean hasMember(Symbol key) {
		return members.containsKey(key);
	}

	@Override
	public void freeze(boolean frozen) {
		this.frozen = frozen;
	}

	@Override
	public void seal(boolean sealed) {
		this.sealed = sealed;
	}

	@Override
	public boolean isFrozen() {
		return this.frozen;
	}

	@Override
	public boolean isSealed() {
		return this.sealed;
	}

}
