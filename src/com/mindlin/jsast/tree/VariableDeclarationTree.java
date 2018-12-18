package com.mindlin.jsast.tree;

import java.util.List;

/**
 * Node representing a variable declaration statement.
 * <p>
 * Note that multiple variables may be declared in this statement; that's the
 * reason for the separation between this and {@link VariableDeclaratorTree}:
 * all declarators under this declaration share the same var/let/const keyword.
 * </p>
 * 
 * @see VariableDeclaratorTree
 * @author mailmindlin
 */
public interface VariableDeclarationTree extends DeclarationStatementTree, VariableDeclarationOrPatternTree {
	
	VariableDeclarationKind getDeclarationStyle();
	
	List<VariableDeclaratorTree> getDeclarations();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VARIABLE_DECLARATION;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitVariableDeclaration(this, data);
	}
	
	public static enum VariableDeclarationKind {
		VAR(false),
		LET(true),
		CONST(true),
		;
		
		private final boolean scoped;
		
		private VariableDeclarationKind(boolean scoped) {
			this.scoped = scoped;
		}
		
		public boolean isScoped() {
			return this.scoped;
		}
	}
}