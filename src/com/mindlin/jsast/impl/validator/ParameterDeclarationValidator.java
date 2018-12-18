package com.mindlin.jsast.impl.validator;

import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.impl.parser.JSDialect;
import com.mindlin.jsast.impl.validator.ErrorReporter.ErrorLevel;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.SignatureDeclarationTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.util.TreePath;

public class ParameterDeclarationValidator implements StatelessValidator<SignatureDeclarationTree> {
	public JSDialect dialect;
	public ParameterDeclarationValidator(JSDialect dialect) {
		this.dialect = dialect;
	}
	
	@Override
	public boolean test(TreePath<? extends Tree> node) {
		return node.current() instanceof SignatureDeclarationTree;
	}
	
	@Override
	public void check(SignatureDeclarationTree node, ErrorReporter reporter) {
		List<ParameterTree> params = node.getParameters();
		if (params.isEmpty())
			return;
		
		int i = 0;
		boolean prevOptional = false;
		for (ParameterTree param : params) {
			Modifiers modifiers = param.getModifiers();
			PatternTree identifier = param.getIdentifier();
			
			if (identifier.getKind() == Tree.Kind.IDENTIFIER && Objects.equals(((IdentifierTree) identifier).getName(), "this")) {
				if (!dialect.supports("ts.parameters.this"))
					reporter.report(ErrorLevel.ERROR, param, "TS this-parameters not supported");
				if (modifiers.isOptional())
					reporter.report(ErrorLevel.WARNING, param, "I'm pretty sure this-parameters can't be optional");
				if (param.getType() == null)
					reporter.report(ErrorLevel.WARNING, param, "This-parameters should have a type");
			}
			
			// Required parameter may not follow optional parameter(s)
			if (prevOptional && !modifiers.isOptional())
				reporter.report(ErrorLevel.ERROR, param, "A required parameter may not follow an optional parameter");
			else if (param.getModifiers().isOptional())
				prevOptional = true;
			
			Modifiers whitelist = Modifiers.union(Modifiers.MASK_VISIBILITY, Modifiers.OPTIONAL, Modifiers.READONLY);
			
			if (param.isRest()) {
				if (modifiers.isOptional())
					reporter.report(ErrorLevel.ERROR, param, "Rest parameters can't be optional");
				if (param.getInitializer() != null)
					reporter.report(ErrorLevel.ERROR, param.getInitializer(), "Rest parameters can't have initializers");
				if (i != params.size() - 1)
					//TODO: fix target here?
					reporter.report(ErrorLevel.ERROR, param, "Rest parameters have to be at the end");
				
				if (Modifiers.intersection(modifiers, Modifiers.MASK_VISIBILITY).any())
					reporter.report(ErrorLevel.ERROR, param, "Rest parameters can't have visibility");
				if (modifiers.isReadonly())
					reporter.report(ErrorLevel.ERROR, param, "Rest parameters can't be readonly");
			} else if (param.getInitializer() != null) {
				if (!dialect.supports("js.parameter.default"))
					reporter.report(ErrorLevel.ERROR, param, "Parameter initializers not supported");
			}
			
			if (modifiers.subtract(whitelist).any()) {
				reporter.report(ErrorLevel.ERROR, param, "Illegal modifier(s): %s", modifiers.subtract(whitelist));
			}
			
			i++; // Keep track of index
		}
	}
	
}
