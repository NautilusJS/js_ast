package com.mindlin.jsast.harness;

import java.util.stream.Stream;

import com.mindlin.jsast.tree.CompilationUnitTree;

public class TransformPass implements CompilerPass<CompilationUnitTree, CompilationUnitTree> {
	
	public TransformPass(CompilerOptions options) {
	}
	
	@Override
	public String getName() {
		return "transform";
	}
	
	public CompilationUnitTree processOne(CompilationUnitTree source) {
		return source;
	}

	@Override
	public Stream<CompilationUnitTree> process(Stream<CompilationUnitTree> sources) throws Exception {
		return sources.map(this::processOne);
	}
	
}
