package com.mindlin.jsast.impl.runtime.objects;

public class Symbol {
	public static final Symbol callSite = new Symbol("Symbol.callSite");
	public static final Symbol constructor = new Symbol("Symbol.constructor");
	private final String description;

	protected Symbol(Object description) {
		this.description = "" + description;
	}

	@Override
	public String toString() {
		return "Symbol(" + description + ")";
	}
}
