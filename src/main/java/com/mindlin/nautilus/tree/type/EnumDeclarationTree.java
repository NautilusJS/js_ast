package com.mindlin.nautilus.tree.type;

import java.util.List;

import com.mindlin.nautilus.tree.DeclarationStatementTree;
import com.mindlin.nautilus.tree.Modifiers;
import com.mindlin.nautilus.tree.NamedDeclarationTree;
import com.mindlin.nautilus.tree.StatementTreeVisitor;
import com.mindlin.nautilus.tree.Tree;

public interface EnumDeclarationTree extends NamedDeclarationTree, DeclarationStatementTree {
	Modifiers getModifiers();
	
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
