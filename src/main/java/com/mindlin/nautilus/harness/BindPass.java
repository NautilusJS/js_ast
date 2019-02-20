package com.mindlin.nautilus.harness;

import java.util.stream.Stream;

import com.mindlin.nautilus.tree.CompilationUnitTree;

public class BindPass implements CompilerPass<CompilationUnitTree, CompilationUnitTree> {
	
	public BindPass(CompilerOptions options) {
	}
	
	@Override
	public String getName() {
		return "bind";
	}
	
	public CompilationUnitTree processOne(CompilationUnitTree source) {
		return source;
	}

	@Override
	public Stream<CompilationUnitTree> process(Stream<CompilationUnitTree> sources) throws Exception {
		return sources.map(this::processOne);
	}
	
}
