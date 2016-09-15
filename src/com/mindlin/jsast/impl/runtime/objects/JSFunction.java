package com.mindlin.jsast.impl.runtime.objects;

@FunctionalInterface
public interface JSFunction {
	Object invoke(Object thiz, Object...params);
}