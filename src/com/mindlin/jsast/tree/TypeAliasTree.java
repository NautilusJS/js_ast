package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.GenericParameterTree;
import com.mindlin.jsast.tree.type.TypeTree;

/**
 * Statement declaring a TypeScript type alias. Written in form
 * {@code type ALIASNAME = TYPE}, where ALIASNAME is an identifier
 * representing the name of the alias, and TYPE is a type statement.
 * 
 * @author Liam
 *
 */
public interface TypeAliasTree extends StatementTree {
	/**
	 * @return name of alias
	 */
	IdentifierTree getAlias();

	/**
	 * @return generic parameters (not null)
	 */
	List<GenericParameterTree> getGenericParameters();
	
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
