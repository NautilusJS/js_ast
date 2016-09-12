package com.mindlin.jsast.impl.util;

/**
 * An union type. Yes, there are times that you need these, and they're definitely better than
 * just returning {@link java.lang.Object Object} or some other generic supertype.
 * @author mailmindlin
 *
 * @param <A>
 * @param <B>
 */
public class Union<A, B> {
	public static <A> Union<A, ?> ofA(A a) {
		return new Union<>(a, true);
	}
	public static <B> Union<?, B> ofB(B b) {
		return new Union<>(b, false);
	}
	final Object value;
	final boolean isA;
	public Union(Object a, boolean isA) {
		value = a;
		this.isA = isA;
	}
	@SuppressWarnings("unchecked")
	public A getA() {
		return (A) value;
	}
	
	@SuppressWarnings("unchecked")
	public B getB() {
		return (B) value;
	}
	
	public boolean isA() {
		return isA;
	}
	
	public boolean isB() {
		return !isA;
	}
}
