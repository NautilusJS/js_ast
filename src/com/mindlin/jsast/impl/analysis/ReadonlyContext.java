package com.mindlin.jsast.impl.analysis;

import java.util.List;

public interface ReadonlyContext extends TypeContext {
	ReadonlyContext getParent();
	
	VariableInfo getThis();
	VariableInfo getSuper();
	VariableInfo getArguments();
	VariableInfo getVar(String name);
	
	boolean isStrict();
	
	/**
	 * Creates an immutable snapshot of this context.
	 * <p>
	 * While {@link ReadonlyContext}s can't be modified on the surface, they contain data strucutres
	 * that could be modified, so {@link #snapshot()} creates a deep clone.
	 * Note that calling {@link #snapshot()} on the same {@link ReadonlyContext} without modifications between
	 * <strong>MAY</strong> return the same object.
	 * </p>
	 * @return snapshot of context
	 */
	ReadonlyContext snapshot();
	
	List<ASTAnnotation> getAnnotations();
}
