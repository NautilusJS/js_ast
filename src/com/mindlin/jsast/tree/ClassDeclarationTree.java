package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.GenericTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public interface ClassDeclarationTree extends ExpressionTree, StatementTree {
	/**
	 * Get class identifier name, else null if not set
	 */
	IdentifierTree getIdentifier();
	
	/**
	 * Get the class that this class extends, if available.
	 * @return super type, else null if not applicable
	 */
	TypeTree getSuperType();
	
	/**
	 * Get the interfaces that this class implements
	 */
	List<TypeTree> getImplementing();
	
	/**
	 * Get the properties for this class
	 */
	List<ClassPropertyTree<?>> getProperties();
	
	/**
	 * Get the generic parameters on this class
	 * @return
	 */
	List<GenericTypeTree> getGenerics();
	
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
		
		if (other == null || this.getKind() == other.getKind() && !(other instanceof ClassDeclarationTree) || this.hashCode() != other.hashCode())
			return false;
		
		
		ClassDeclarationTree o = (ClassDeclarationTree) other;
		
		return this.isAbstract() == o.isAbstract()
			&& Tree.equivalentTo(this.getIdentifier(), o.getIdentifier())
			&& Tree.equivalentTo(this.getGenerics(), o.getGenerics())
			&& Tree.equivalentTo(this.getSuperType(), o.getSuperType())
			&& Tree.equivalentTo(this.getImplementing(), o.getImplementing())
			&& Tree.equivalentTo(this.getProperties(), o.getProperties());//TODO fix for order
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitClassDeclaration(this, data);
	}
}
