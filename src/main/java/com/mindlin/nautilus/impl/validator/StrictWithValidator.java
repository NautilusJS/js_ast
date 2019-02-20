package com.mindlin.jsast.impl.validator;

import com.mindlin.jsast.impl.validator.ErrorReporter.ErrorLevel;
import com.mindlin.jsast.tree.SignatureDeclarationTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.util.TreePath;

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
