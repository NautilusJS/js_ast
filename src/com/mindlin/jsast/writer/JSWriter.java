package com.mindlin.jsast.writer;

import java.io.IOException;
import java.io.Writer;

import com.mindlin.jsast.tree.CompilationUnitTree;

public interface JSWriter {
	void write(CompilationUnitTree src, Writer out) throws IOException;
}
