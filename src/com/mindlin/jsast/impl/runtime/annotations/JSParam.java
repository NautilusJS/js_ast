package com.mindlin.jsast.impl.runtime.annotations;

public @interface JSParam {
	boolean optional() default false;
	String defaultValue() default "";
}