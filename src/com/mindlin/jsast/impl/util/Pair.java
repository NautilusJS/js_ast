package com.mindlin.jsast.impl.util;

public class Pair<A, B> {
	protected final A a;
	protected final B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A left() {
		return this.a;
	}
	
	public B right() {
		return this.b;
	}
}
