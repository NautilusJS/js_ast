package com.mindlin.jsast.impl.validator;

import com.mindlin.jsast.tree.Tree;

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
