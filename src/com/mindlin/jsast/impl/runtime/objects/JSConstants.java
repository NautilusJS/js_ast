package com.mindlin.jsast.impl.runtime.objects;

public final class JSConstants {
	@JSGlobal(name="undefined")
	public static final Object UNDEFINED = new Object() {
		@Override
		public String toString() {
			return "undefined";
		}
	}
}