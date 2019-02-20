package com.mindlin.jsast.transform;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mindlin.jsast.tree.CompilationUnitTree;

public class TransformerSeries implements Function<CompilationUnitTree, CompilationUnitTree> {
	protected final List<ASTTransformer<?>> transformers;
	
	@SuppressWarnings("unchecked")
	public TransformerSeries(TreeTransformation<?>...transformations) {
		this.transformers = Arrays.stream(transformations)
			.map((Function<TreeTransformation<?>, ASTTransformer<?>>) ASTTransformer::new)
			.collect(Collectors.toList());
	}

	@Override
	public CompilationUnitTree apply(CompilationUnitTree ast) {
		CompilationUnitTree ast0 = ast;
		//TODO cache intermediate forms, to (hopefully) get us out of unstable results.
		int n = 0;
		while (true) {
			CompilationUnitTree ast1 = ast0;
			for (ASTTransformer<?> transformer : transformers)
				ast1 = (CompilationUnitTree) ast1.accept(transformer, null);
			n++;
			if (ast1 == ast0 || ast0.hashCode() == ast1.hashCode() && (ast1.equivalentTo(ast0) || ast0.equivalentTo(ast1))) {
				System.out.format("%d compiler passes.\n", n);
				return ast0;
			}
			ast0 = ast1;
		}
	}
	
}
