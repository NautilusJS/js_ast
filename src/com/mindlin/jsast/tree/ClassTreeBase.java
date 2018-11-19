package com.mindlin.jsast.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;

public interface ClassTreeBase extends Tree {
	Modifiers getModifiers();
	/**
	 * Get class identifier name
	 */
	IdentifierTree getName();
	
	/**
	 * Get the generic parameters on this class
	 * @return
	 */
	List<TypeParameterDeclarationTree> getTypeParameters();
	
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
	List<ClassElementTree> getProperties();
	
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
		
		return Objects.equals(this.getModifiers(), o.getModifiers())
			&& Tree.equivalentTo(this.getName(), o.getName())
			&& Tree.equivalentTo(this.getTypeParameters(), o.getTypeParameters())
			&& Tree.equivalentTo(this.getSuperType(), o.getSuperType())
			&& Tree.equivalentTo(this.getImplementing(), o.getImplementing())
			&& Tree.equivalentTo(this.getProperties(), o.getProperties());//TODO fix for order
	}
	
	public static interface ClassExpressionTree extends ClassTreeBase, ExpressionTree {
		@Override
		default Kind getKind() {
			return Tree.Kind.CLASS_EXPRESSION;
		}
		
		@Override
		default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
			return visitor.visitClassExpression(this, data);
		}
	}
	
	public static interface ClassDeclarationTree extends ClassTreeBase, DecoratableTree, NamedDeclarationTree, DeclarationStatementTree {
		/**
		 * May be null in some cases (e.g., {@code export default class ...}).
		 */
		@Override
		IdentifierTree getName();
		
		@Override
		default Kind getKind() {
			return Tree.Kind.CLASS_DECLARATION;
		}
		
		@Override
		default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
			return visitor.visitClassDeclaration(this, data);
		}
	}
}
