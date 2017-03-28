package com.mindlin.jsast.json.api;

import java.io.IOException;

public interface JSONInput extends SafelyCloseable {
	JSONObjectInput readObject() throws IOException;
	
	<T> T readObject(Class<? extends T> clazz) throws IOException;

	JSONArrayInput readArray() throws IOException;

	SafelyCloseable mark() throws IOException;
}
