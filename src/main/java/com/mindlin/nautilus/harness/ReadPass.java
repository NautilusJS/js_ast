package com.mindlin.nautilus.harness;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.mindlin.nautilus.fs.SourceFile;
import com.mindlin.nautilus.fs.SourceFile.NominalSourceFile;
import com.mindlin.nautilus.impl.lexer.JSLexer;
import com.mindlin.nautilus.impl.parser.JSDialect;
import com.mindlin.nautilus.impl.parser.JSParser;
import com.mindlin.nautilus.tree.CompilationUnitTree;

public class ReadPass implements CompilerPass<Path, CompilationUnitTree> {
	List<CompilerStage> stages;
	public ReadPass(CompilerOptions options) {
		stages = Arrays.asList(new ReadStage(options), new ParseStage(options));
	}
	
	@Override
	public String getName() {
		return "read";
	}
	
	@SuppressWarnings("unchecked")
	public CompilationUnitTree processOne(Path source) {
		Object result = source;
		try {
			for (CompilerStage stage : stages)
				result = stage.process(result);
		} catch (Exception e) {
			//TODO: fix?
			throw new RuntimeException(e);
		}
		return (CompilationUnitTree) result;
	}

	@Override
	public Stream<CompilationUnitTree> process(Stream<Path> sources) throws Exception {
		return sources.map(this::processOne);
	}
	
	public static class ReadStage extends CompilerStage<Path, SourceFile> {
		protected final Charset encoding;
		
		public ReadStage(CompilerOptions options) {
			this.encoding = options.get(CompilerOptions.ENCODING).get();
		}
		
		@Override
		public String getName() {
			return "read";
		}

		@Override
		public SourceFile process(Path sourcePath) throws IOException {
			byte[] encoded = Files.readAllBytes(sourcePath);
			String source = new String(encoded, this.encoding);
			//TODO: resolve name
			SourceFile src = new NominalSourceFile(null, source);
			return src;
		}
		
	}
	
	public static class ParseStage extends CompilerStage<SourceFile, CompilationUnitTree> {
		protected final JSParser parser;
		
		public ParseStage(CompilerOptions options) {
			JSDialect dialect = options.get(CompilerOptions.SOURCE_LANGUAGE).get();
			this.parser = new JSParser(dialect);
		}
		
		@Override
		public String getName() {
			return "parse";
		}

		@Override
		public CompilationUnitTree process(SourceFile source) throws IOException {
			JSLexer lexer = new JSLexer(source);
			CompilationUnitTree tree = this.parser.apply(source.getName(), lexer);
			return tree;
		}
		
	}
	
}
