package com.mindlin.jsast.impl.runtime.objects;

import com.mindlin.jsast.impl.runtime.annotations.JSGlobal;

public final class JSConstants {
	@JSGlobal("undefined")
	public static final Object UNDEFINED = new Object() {
		@Override
		public String toString() {
			return "undefined";
		}
	};
	
	@JSGlobal("Infinity")
	public static final Object INFINITY = Double.POSITIVE_INFINITY;
	
	@JSGlobal("NaN")
	public static final Object NAN = Double.NaN;
	
	@JSGlobal("Symbol")
	public static JSFunction SYMBOL = new JSFunction() {
		@Override
		public Object invoke(Object thiz, Object... params) {
			return new Symbol(params.length > 0 ? params[0] : "");
		}
	};
}