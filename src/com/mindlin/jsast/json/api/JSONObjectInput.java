package com.mindlin.jsast.json.api;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public interface JSONObjectInput extends SafelyCloseable, Iterator<Map.Entry<String, ? extends Object>> {
	boolean readAll();
	
	/**
	 * Get the next key
	 * @return
	 */
	String nextKey();
	
	
	<T> T mapTo(Class<? extends T> clazz);
	
	
	boolean canReadBoolean();
	
	boolean getBoolean();
	
	boolean optBoolean();
	
	boolean optBoolean(boolean defaultValue);
	
	
	boolean canReadNull();
	
	Void readNull();
	
	Optional<Void> optNull();
	
	
	boolean canReadNumber();
	
	Number getNumber();
	
	Optional<Number> optNumber();
	
	default Number getNumber(Number defaultValue) {
		return optNumber().orElse(defaultValue);
	}
	
	
	boolean canReadString();
	
	String getString();
	
	Optional<String> optString();
	
	default String optString(String defaultValue) {
		return optString().orElse(defaultValue);
	}
	
	
	boolean canReadObject();
	
	JSONObjectInput getObject();
	
	Optional<JSONObjectInput> optObject();
	
	<C> boolean canReadObject(Class<? extends C> clazz);
	
	<C> C readObject(Class<? extends C> clazz);
	
	<C> Optional<C> optObject(Class<? extends C> clazz);
	
	default <C> C optObject(Class<? extends C> clazz, C defaultValue) {
		return this.<C>optObject(clazz)
			.orElse(defaultValue);
	}
	
	
	boolean canReadArray();
	
	JSONArrayInput getArray();
	
	Optional<JSONArrayInput> optArray();
	
	
}
