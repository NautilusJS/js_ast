package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.annotation.Optional;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public interface ClassTreeBase extends Tree {
	/**
	 * Get class identifier name
	 */
	@Optional IdentifierTree getIdentifier();
	
	/**
	 * Get the generic parameters on this class
	 * @return
	 */
	List<TypeParameterDeclarationTree> getGenerics();
	
	/**
	 * Get the class that this class extends, if available.
	 * @return super type, else null if not applicable
	 */
	@Optional
	TypeTree getSuperType();
	
	/**
	 * Get the interfaces that this class implements
	 */
	List<TypeTree> getImplementing();
	
	/**
	 * Get the properties for this class
	 */
	List<ClassElementTree> getProperties();
	
	/**
	 * Get if this class is abstract
	 */
	boolean isAbstract();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CLASS_DECLARATION;
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other)
			return true;
		
		if (other == null || this.getKind() == other.getKind() && !(other instanceof ClassTreeBase) || this.hashCode() != other.hashCode())
			return false;
		
		
		ClassTreeBase o = (ClassTreeBase) other;
		
		return this.isAbstract() == o.isAbstract()
			&& Tree.equivalentTo(this.getIdentifier(), o.getIdentifier())
			&& Tree.equivalentTo(this.getGenerics(), o.getGenerics())
			&& Tree.equivalentTo(this.getSuperType(), o.getSuperType())
			&& Tree.equivalentTo(this.getImplementing(), o.getImplementing())
			&& Tree.equivalentTo(this.getProperties(), o.getProperties());//TODO fix for order
	}
}
