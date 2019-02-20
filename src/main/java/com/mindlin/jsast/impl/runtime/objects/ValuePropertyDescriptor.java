package com.mindlin.jsast.impl.runtime.objects;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ValuePropertyDescriptor implements PropertyDescriptor {
	boolean writable, enumerable, configurable;
	Object value;
	public ValuePropertyDescriptor() {
		
	}
	public ValuePropertyDescriptor(Object value, boolean writable, boolean enumerable, boolean configurable) {
		this.value = value;
		this.writable = writable;
		this.enumerable = enumerable;
		this.configurable = configurable;
	}
	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public boolean isEnumarable() {
		return enumerable;
	}

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public Consumer<Object> setter() {
		return (v)->{
			if (!isWritable())
				return;
			value = v;
		};
	}
	
	@Override
	public Supplier<Object> getter() {
		return ()->value;
	}

	@Override
	public Optional<Object> value() {
		return Optional.ofNullable(value);
	}

}
