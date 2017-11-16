package com.mindlin.jsast.impl.runtime.objects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.mindlin.jsast.impl.runtime.annotations.JSProperty;
import com.mindlin.jsast.impl.runtime.annotations.JSStaticCallSite;

public class StaticWrapper extends AbstractJSObject {
	protected final Class<?> clazz;

	public StaticWrapper(Class<?> clazz) {
		super(new HashMap<>());
		this.clazz = clazz;
		for (Method method : clazz.getMethods()) {
			if (!Modifier.isStatic(method.getModifiers()))
				continue;
			if (method.isAnnotationPresent(JSProperty.class)) {
				JSProperty meta = method.getAnnotation(JSProperty.class);
				String name = meta.name();
				if (name.isEmpty())
					name = method.getName();
				members.put(name,
						new ValuePropertyDescriptor(method, meta.writable(), meta.configurable(), meta.enumerable()));
			}
			if (method.isAnnotationPresent(JSStaticCallSite.class))
				members.put(Symbol.callSite, new ValuePropertyDescriptor(method, false, false, false));
		}
		for (Field field : clazz.getFields()) {
			if (!(Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(JSProperty.class)))
				continue;
			JSProperty meta = field.getAnnotation(JSProperty.class);
			members.put(meta.name().isEmpty() ? field.getName() : meta.name(),
					new FieldPropertyDescriptor(field, null, meta));
		}
		//TODO support constructor(s)
	}

	@Override
	public boolean isInstanceOf(Object other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "class " + clazz.getSimpleName();
	}

	@Override
	public boolean isInstance(Object maybeChild) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getClassName() {
		return clazz.getSimpleName();
	}
}
