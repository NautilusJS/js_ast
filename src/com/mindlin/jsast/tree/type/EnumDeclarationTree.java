package com.mindlin.jsast.tree.type;

import java.util.List;

import com.mindlin.jsast.tree.DeclarationStatementTree;
import com.mindlin.jsast.tree.NamedDeclarationTree;
import com.mindlin.jsast.tree.StatementTreeVisitor;
import com.mindlin.jsast.tree.Tree;

public interface EnumDeclarationTree extends NamedDeclarationTree, DeclarationStatementTree {
	/**
	 * If const enum.
	 * @return
	 */
	boolean isConst();
	
	/**
	 * List members in order
	 * @return
	 */
	List<EnumMemberTree> getMembers();

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.ENUM_DECLARATION;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitEnumDeclaration(this, data);
	}
}
