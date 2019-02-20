package com.mindlin.jsast.impl.runtime.objects;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mindlin.jsast.exception.JSTypeError;
import com.mindlin.jsast.impl.runtime.annotations.JSProperty;

public class FieldPropertyDescriptor implements PropertyDescriptor {
	protected final Field f;
	protected final Object self;
	protected boolean writable;
	protected boolean configurable;
	protected boolean enumerable;

	public FieldPropertyDescriptor(Field f, Object self, boolean writable, boolean configurable, boolean enumerable) {
		this.f = f;
		this.self = self;
		this.writable = writable;
		this.configurable = configurable;
		this.enumerable = enumerable;
	}

	public FieldPropertyDescriptor(Field f, Object self, JSProperty data) {
		this(f, self, data.writable(), data.configurable(), data.enumerable());
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
		return (v) -> {
			try {
				f.set(self, v);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new JSTypeError(e);
			}
		};
	}

	@Override
	public Supplier<Object> getter() {
		return () -> {
			try {
				return (f.get(self));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new JSTypeError(e);
			}
		};
	}

	@Override
	public Optional<Object> value() {
		return Optional.empty();
	}

}
