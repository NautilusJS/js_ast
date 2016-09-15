package com.mindlin.jsast.json.api;

public interface JSONInput extends SafelyCloseable {
	JSONObjectInput readObject();

	JSONArrayInput readArray();

	SafelyCloseable mark();
}
