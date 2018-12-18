package com.mindlin.jsast.harness;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Compiler {
	protected CompilerOptions options;
	protected List<CompilerPass> passes;
	
	public Compiler(CompilerOptions options, List<CompilerPass> passes) {
		this.options = options;
		this.passes = passes;
	}
	
	public Stream<Path> getSourceFiles() throws IOException {
		return Files.find(Paths.get(""), 1, (p, fa) -> p.getFileName().toString().endsWith("x.ts"));
	}
	
	public void compile() throws Exception {
		Stream<Path> sourceFiles = this.getSourceFiles();
		Stream<?> result = (Stream<?>) sourceFiles;
		for (CompilerPass pass : passes)
			result = pass.process(result);
		List<?> x = result.collect(Collectors.toList());
		System.out.println(x);
	}
	
}
