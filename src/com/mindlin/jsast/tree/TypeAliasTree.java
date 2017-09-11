package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.GenericTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

/**
 * Statement declaring a TypeScript type alias. Written in form
 * <code>type ALIASNAME = TYPE</code>, where ALIASNAME is an identifier
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
	 * @return generic parameters
	 */
	List<GenericTypeTree> getGenericParameters();
	
	TypeTree getValue();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TYPE_ALIAS;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitTypeAlias(this, data);
	}
}
