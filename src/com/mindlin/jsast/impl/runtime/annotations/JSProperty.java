package com.mindlin.jsast.impl.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
public @interface JSProperty {
	String name() default "";

	boolean configurable() default false;

	boolean enumerable() default false;

	boolean writable() default false;
}