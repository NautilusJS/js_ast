package com.mindlin.nautilus.impl.validator;

import com.mindlin.nautilus.impl.validator.ErrorReporter.ErrorLevel;
import com.mindlin.nautilus.tree.SignatureDeclarationTree;
import com.mindlin.nautilus.tree.Tree;
import com.mindlin.nautilus.tree.Tree.Kind;
import com.mindlin.nautilus.tree.util.TreePath;

public class StrictWithValidator implements StatelessValidator<SignatureDeclarationTree> {
	
	public StrictWithValidator() {
	}

	@Override
	public boolean test(TreePath<? extends Tree> node) {
		return node.current().getKind() == Kind.WITH;
	}

	@Override
	public void check(SignatureDeclarationTree node, ErrorReporter reporter) {
		reporter.report(ErrorLevel.ERROR, node, "'with' statements not allowed in strict mode");
	}
	
}
