package com.mindlin.jsast.impl.writer;

import java.io.IOException;
import java.io.Writer;

import com.mindlin.jsast.tree.CompilationUnitTree;

public interface JSWriter {
	void write(CompilationUnitTree tree, Writer output) throws IOException;
}
