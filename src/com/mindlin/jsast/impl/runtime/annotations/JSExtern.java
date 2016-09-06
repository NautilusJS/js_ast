package com.mindlin.jsast.impl.runtime.annotations;

public @interface JSExtern {
	String name() default "";
	boolean configurable() default false;
	boolean enumerable() default false;
	boolean writable() default false;
}