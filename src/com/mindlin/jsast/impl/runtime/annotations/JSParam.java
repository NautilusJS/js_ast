package com.mindlin.jsast.impl.runtime.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface JSParam {
	boolean optional() default false;
	String defaultValue() default "undefined";
}