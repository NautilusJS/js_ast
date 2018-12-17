package com.mindlin.jsast.impl.analysis;

import java.util.List;
import java.util.Set;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.type.Type;
import com.mindlin.jsast.type.TypeParameter;

/**
 * A context that can be changed over time.
 * 
 * @author mailmindlin
 */
public interface DynamicContext extends ReadonlyContext {
	@Override
	DynamicContext getParent();
	
	//Type stuff
	/**
	 * Register a type alias.
	 * @param name Alias name
	 * @param parameters Generic parameter variables on alias
	 * @param type type that alias resolves to (with generic variables resolved)
	 */
	void aliasType(String name, List<TypeParameter> parameters, Type type);
	void defineGeneric(TypeParameterDeclarationTree generic);
	void declare(String name, boolean isScoped, boolean isAssignable, Type type);
	
	//Variable stuff
	void define(String name, boolean isScoped, boolean isAssignable, Type type);
	/**
	 * Mark assignment
	 * @param variable variable to assign
	 * @param type type assigned to
	 * @param valueControlEdges variables that this variable is dependent on
	 */
	void assign(VariableInfo variable, Type type);
	
	/**
	 * Set strict flag
	 * @param strict
	 */
	void setStrict(boolean strict);
	
	//Scope stuff
	/**
	 * Push a block scope.
	 * Captures let and const declarations.
	 */
	DynamicContext pushBlock();
	
	Set<VariableInfo> getCfControlEdges();
	/**
	 * Push a `for` scope.
	 * Any looping variables should be declared in this scope.
	 * Usually a block scope is pushed after this.
	 */
	DynamicContext pushLoop();
	
	/**
	 * Push a `class` scope.
	 * Automatically registers 'this' and 'super' variables 
	 * @return
	 */
	DynamicContext pushClass(boolean hasGenerics);
	
	/**
	 * Push a `function` scope.
	 * Pushes an 'arguments' variable.
	 * Maybe push a 'this' varaible.
	 * Parameters should be pushed in this scope.
	 * A block scope should then be pushed.
	 * 
	 * Captures let, const, var, and function declarations.
	 * @param hasThis Whether or not to push a 'this' variable.
	 * @return
	 */
	DynamicContext pushFunction(boolean hasThis, boolean hasGenerics);
	
	/**
	 * Annotate a node
	 * @param level
	 * @param node
	 * @param message
	 */
	void annotate(ASTAnnotation.AnnotationLevel level, Tree node, String message);
	
	default void note(Tree node, String message) {
		annotate(ASTAnnotation.AnnotationLevel.HINT, node, message);
	}
	
	default void warn(Tree node, String message) {
		annotate(ASTAnnotation.AnnotationLevel.WARNING, node, message);
	}
	
	default void error(Tree node, String message) {
		annotate(ASTAnnotation.AnnotationLevel.ERROR, node, message);
	}
}
