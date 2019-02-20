package com.mindlin.nautilus.tree.type;

import java.util.List;

import com.mindlin.nautilus.tree.DeclarationStatementTree;
import com.mindlin.nautilus.tree.IdentifierTree;
import com.mindlin.nautilus.tree.NamedDeclarationTree;
import com.mindlin.nautilus.tree.StatementTreeVisitor;
import com.mindlin.nautilus.tree.Tree;

/**
 * Statement declaring a TypeScript type alias. Written in form
 * {@code type ALIASNAME = TYPE}, where ALIASNAME is an identifier
 * representing the name of the alias, and TYPE is a type statement.
 * 
 * @author mailmindlin
 */
public interface TypeAliasTree extends NamedDeclarationTree, DeclarationStatementTree {
	@Override
	IdentifierTree getName();
	
	/**
	 * @return generic parameters (not null)
	 */
	List<TypeParameterDeclarationTree> getTypeParameters();
	
	TypeTree getValue();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TYPE_ALIAS;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitTypeAlias(this, data);
	}
}
