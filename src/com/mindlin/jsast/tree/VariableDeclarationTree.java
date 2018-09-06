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
public interface VariableDeclarationTree extends DeclarationTree, PatternTree {
	List<VariableDeclaratorTree> getDeclarations();
	
	/**
	 * If variables declared under this tree are declared in the block scope.
	 * In other terms, whether this was initialized with a {@code let}
	 * or {@code const} keyword.
	 * <p>
	 * If this is true, the variables declared under this tree are limited to
	 * the scope of the block, statement, or expression on which it is used.
	 * </p>
	 * 
	 * @return if scoped
	 * 
	 * @see <a href=
	 *      "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/let">MDN
	 *      Article on let Keyword</a>
	 */
	boolean isScoped();
	
	/**
	 * If variables declared under this tree are constants. More specifically,
	 * whether this was initialized with a {@code const} keyword.
	 * <p>
	 * If this method returns {@code true}, all declarations MUST have an
	 * initializer.
	 * </p>
	 * 
	 * @return if const
	 */
	boolean isConst();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.VARIABLE_DECLARATION;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitVariableDeclaration(this, data);
	}
}