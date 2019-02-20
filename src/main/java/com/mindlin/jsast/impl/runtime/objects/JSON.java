package com.mindlin.jsast.impl.runtime.objects;

import com.mindlin.jsast.impl.runtime.annotations.JSExtern;
import com.mindlin.jsast.impl.runtime.annotations.JSProperty;
import com.mindlin.jsast.json.JSONUtils;

@JSExtern(isDefault = true)
public class JSON {
	@JSExtern
	@JSProperty
	public static Object parse(String t) {
		// TODO finish
		throw new UnsupportedOperationException();
	}

	@JSExtern
	@JSProperty
	public static String stringify(Object o) {
		return JSONUtils.serialize(o);
	}
	
	private JSON() {
		
	}
}
