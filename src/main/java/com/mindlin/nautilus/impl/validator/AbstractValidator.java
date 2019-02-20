package com.mindlin.nautilus.impl.validator;

import com.mindlin.nautilus.tree.Tree;

/**
 * Base class for AST validators
 * @author mailmindlin
 */
public abstract class AbstractValidator {
	protected final ErrorReporter reporter;
	public AbstractValidator(ErrorReporter reporter) {
		this.reporter = reporter;
	}
	
	public abstract void validate(Tree node);
}
