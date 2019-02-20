package com.mindlin.jsast.impl.analysis;

import java.util.List;

public interface FunctionInfo extends VariableInfo {
	@Override
	default boolean isFunction() {
		return true;
	}
	
	/**
	 * If is pure function.
	 * A pure function has the following properties:
	 * <ul>
	 * <li>It has no side effects</li>
	 * <li>It's deterministic: for some {@code x}, {@code f(x) == f(x)}</li>
	 * </ul>
	 * TODO: maybe require halting?
	 * @return if function is pure
	 */
	boolean isPure();
	/**
	 * Stricter form of {@link #isPure()}. In addition to those requirements, a const function,
	 * if called with constant arguments, should be computable at compile-time.
	 * @return if function is const
	 */
	boolean isConst();
	
	
	List<VariableInfo> getDependencies();
}
