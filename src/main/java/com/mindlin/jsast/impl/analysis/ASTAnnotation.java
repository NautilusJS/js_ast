package com.mindlin.jsast.impl.analysis;

import java.io.Serializable;

import com.mindlin.jsast.tree.Tree;

/**
 * Annotation attached to tree.
 * @author mailmindlin
 */
public class ASTAnnotation implements Serializable {
	private static final long serialVersionUID = -3446544558007375584L;
	
	protected AnnotationLevel level;
	protected Tree target;
	protected String message;
	
	public ASTAnnotation(AnnotationLevel level, Tree target, String message) {
		this.level = level;
		this.target = target;
		this.message = message;
	}
	
	public static enum AnnotationLevel {
		HINT,
		WARNING,
		ERROR;
	}
}
