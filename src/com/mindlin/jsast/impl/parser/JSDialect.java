package com.mindlin.jsast.impl.parser;

import com.mindlin.jsast.exception.JSUnsupportedException;

public interface JSDialect {
	boolean supports(String feature);
	default void require(String feature, long position) {
		if (!supports(feature))
			throw new JSUnsupportedException(feature, position);
	}
	default boolean supportsTypes() {
		return supports("extension.types");
	}
	default boolean supportsLambdas() {
		return supports("js.function.lambda");
	}
	default boolean supportsModule() {
		return supports("js.module");
	}
	default boolean supportsYield() {
		return supports("js.yield");
	}
	public static enum JSStandardDialect implements JSDialect {
		ES5 {
			public boolean supports(String feature) {
				switch (feature) {
					case "js.accessors":
					case "js.strict":
					case "js.json":
						return true;
				}
				return false;
			}
		},
		ES6 {
			public boolean supports(String feature) {
				switch (feature) {
					case "js.module":
					case "js.parameter.defaultValue":
					case "js.parameter.rest":
					case "js.variable.scoped":
					case "js.variable.const":
					case "js.function.lambda":
					case "js.operator.spread":
					case "js.literal.octal":
					case "js.literal.binary":
					case "js.literal.template":
					case "js.literal.unicode":
					case "js.literal.regex.sticky":
					case "js.literal.object.shorthand":
					case "js.literal.object.shorthand.computed":
					case "js.literal.object.methodProperties":
					case "js.desctructuring":
					case "js.yield":
					case "js.class":
					case "js.class.static":
					case "js.class.inheritance":
					case "js.class.constructor":
					case "js.class.super":
					case "js.symbol":
					case "js.forOf":
					case "js.iterator":
					case "js.generator":
						//TODO finish
						return true;
				}
				if (ES5.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT {
			public boolean supports(String feature) {
				switch (feature) {
					case "extension.parameter.optional":
					case "extension.types":
					case "extension.types.union":
					case "extension.types.interface":
					case "extension.types.cast":
						return true;
				}
				if (JSStandardDialect.ES6.supports(feature))
					return true;
				return false;
			}
		};
	}
}
