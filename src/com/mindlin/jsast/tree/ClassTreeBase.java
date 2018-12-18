package com.mindlin.jsast.tree;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;

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
	 * @return Declared extends/implements clauses.
	 */
	List<HeritageClauseTree> getHeritage();
	
	/**
	 * Get the (declared) properties for this class
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
			&& Tree.equivalentTo(this.getHeritage(), o.getHeritage())//TODO: fix for order
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
