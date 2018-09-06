package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.ClassPropertyTree.AccessModifier;
import com.mindlin.jsast.tree.type.TypeTree;

/**
 * Represents a parameter declaration (this is used when defining a function,
 * not when calling one). Supports most of ES6/TypeScript functionality,
 * including being a rest parameter, type annotations, default values, and
 * optional qualities.
 * 
 * TODO Does not yet support destructuring.
 * 
 * @author mailmindlin
 */
public interface ParameterTree extends Tree {
	
	/**
	 * Get the access modifier for this parameter.
	 * 
	 * @return this parameter's access modifier, else {@code null} if not
	 *         present
	 */
	AccessModifier getAccessModifier();
	
	/**
	 * Get if readonly modifier is present.
	 * 
	 * @return Whether or not the {@code readonly} modifier is present on this
	 *         parameter.
	 */
	boolean isReadonly();
	
	/**
	 * Return if this parameter is a rest parameter.
	 * 
	 * @return If this parameter is a rest parameter.
	 */
	boolean isRest();
	
	PatternTree getIdentifier();
	
	/**
	 * If declared as an optional parameter.
	 * 
	 * @return optional
	 */
	boolean isOptional();
	
	/**
	 * Get declared type.
	 * <br/>
	 * Note that if this is an optional parameter, the actual type is implicitly
	 * this type or with {@code undefined}.
	 * 
	 * @return declared type (or null if not declared)
	 */
	TypeTree getType();
	
	/**
	 * Get declared initializer.
	 * 
	 * @return Initializer, or {@code null} if not present
	 */
	ExpressionTree getInitializer();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.PARAMETER;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		throw new UnsupportedOperationException();
	}
}