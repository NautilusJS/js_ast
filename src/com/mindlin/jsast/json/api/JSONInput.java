package com.mindlin.jsast.json.api;

public interface JSONInput extends SafelyCloseable {
	JSONObjectInput readObject();
	
	<T> T readObject(Class<? extends T> clazz);

	JSONArrayInput readArray();

	SafelyCloseable mark();
}
