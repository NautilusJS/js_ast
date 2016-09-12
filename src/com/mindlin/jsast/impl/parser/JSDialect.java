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
			@Override
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
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "js.class":
					case "js.class.constructor":
					case "js.class.inheritance":
					case "js.class.static":
					case "js.class.super":
					case "js.class.this":
					case "js.desctructuring":
					case "js.forOf":
					case "js.function.lambda":
					case "js.generator":
					case "js.iterator":
					case "js.literal.binary":
					case "js.literal.object.methodProperties":
					case "js.literal.object.shorthand":
					case "js.literal.object.shorthand.computed":
					case "js.literal.octal":
					case "js.literal.regex.sticky":
					case "js.literal.template":
					case "js.literal.unicode":
					case "js.module":
					case "js.operator.spread":
					case "js.parameter.default":
					case "js.parameter.rest":
					case "js.symbol":
					case "js.variable.const":
					case "js.variable.scoped":
					case "js.yield":
						//TODO finish
						return true;
				}
				if (ES5.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT {
			@Override
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
		},
		EVERYTHING {
			@Override
			public boolean supports(String feature) {
				return true;
			}
		};
	}
}
