package com.mindlin.jsast.tree;

import java.util.List;
import java.util.Optional;

public interface ClassDeclarationTree extends ExpressionTree, StatementTree {
	/**
	 * Get class identifier name, else null if not set
	 */
	IdentifierTree getIdentifier();
	
	/**
	 * Get the class that this class extends, if available
	 */
	Optional<TypeTree> getSuperType();
	
	/**
	 * Get the interfaces that this class implements
	 */
	List<TypeTree> getImplementing();
	
	/**
	 * Get the properties for this class
	 */
	List<ClassPropertyTree<?>> getProperties();
	
	/**
	 * Get if this class is abstract
	 */
	boolean isAbstract();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CLASS_DECLARATION;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitClassDeclaration(this, data);
	}
}
