package com.mindlin.jsast.impl.parser;

import com.mindlin.jsast.exception.JSUnsupportedException;
import com.mindlin.jsast.fs.SourceRange;

public interface JSDialect {
	boolean supports(String feature);
	default void require(String feature, SourceRange position) {
		if (!supports(feature))
			throw new JSUnsupportedException(feature, position);
	}
	default boolean supportsTypes() {
		return supports("ts.types");
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
					case "js.accessor":
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
					case "js.function.generator":
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
					case "js.property.shorthand":
					case "js.method.shorthand":
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
		ES2017 {
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "js.function.async":
					case "js.await":
						//TODO finish
						return true;
				}
				if (ES6.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT_11 {
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "ts.parameter.optional":
					case "ts.visibility":
					case "ts.visibility.public":
					case "ts.visibility.private":
					case "ts.types":
					case "ts.types.enum":
						return true;
				}
				if (JSStandardDialect.ES5.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT_13 {
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "ts.visibility.protected":
					case "ts.types.tuple":
						return true;
				}
				if (JSStandardDialect.ES5.supports(feature) || JSStandardDialect.TYPESCRIPT_11.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT_14 {
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "ts.types.union":
					case "ts.types.alias":
					case "ts.types.enum.const":
						return true;
				}
				if (JSStandardDialect.ES6.supports(feature) || JSStandardDialect.TYPESCRIPT_13.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT_15 {
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "ts.namespace":
					case "js.decorator":
						return true;
				}
				if (JSStandardDialect.ES6.supports(feature) || JSStandardDialect.TYPESCRIPT_14.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT_16 {
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "ts.types.intersection":
					case "ts.class.abstract":
						return true;
				}
				if (JSStandardDialect.ES6.supports(feature) || JSStandardDialect.TYPESCRIPT_15.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT_17 {
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "ts.types.this":
					case "ts.types.literal.string":
						return true;
				}
				if (JSStandardDialect.ES6.supports(feature) || JSStandardDialect.TYPESCRIPT_16.supports(feature))
					return true;
				return false;
			}
		},
		TYPESCRIPT {
			@Override
			public boolean supports(String feature) {
				switch (feature) {
					case "ts.parameter.optional":
					case "ts.parameter.accessModifier":
					case "ts.types":
					case "ts.types.union":
					case "ts.types.interface":
					case "ts.types.cast":
					case "ts.types.this":
					case "ts.class.abstract":
						return true;
				}
				if (JSStandardDialect.ES2017.supports(feature))
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
