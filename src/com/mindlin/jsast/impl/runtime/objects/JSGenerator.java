package com.mindlin.jsast.impl.runtime.objects;

public interface JSGenerator<T> {
	T next(Object...values);
}
